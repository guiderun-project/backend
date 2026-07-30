package com.guide.run.global.exception;

import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.event.entity.type.EventType;
import com.guide.run.global.config.MessageConfig;
import com.guide.run.global.exception.event.EventLogicExceptionAdvice;
import com.guide.run.global.exception.event.logic.NotValidSortException;
import com.guide.run.global.exception.validation.ValidationExceptionAdvice;
import com.guide.run.global.service.ResponseService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionAdviceTest {
    private static final String TIMESTAMP_PATTERN = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(?:\\.\\d+)?\\+09:00$";
    private static final String UNKNOWN_MESSAGE = "일시적인 오류가 발생했어요. 잠시 후 다시 시도해주세요.";

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        ErrorResponseFactory errorResponseFactory = new ErrorResponseFactory();
        MessageSource messageSource = messageSource();
        mockMvc = MockMvcBuilders
                .standaloneSetup(new ValidationTestController(), new CommonExceptionTestController())
                .setControllerAdvice(
                        new FailResultResponseBodyAdvice(),
                        new EventLogicExceptionAdvice(messageSource, new ResponseService()),
                        new ValidationExceptionAdvice(errorResponseFactory),
                        new GlobalExceptionAdvice(errorResponseFactory, messageSource),
                        new UnknownExceptionAdvice(messageSource, errorResponseFactory)
                )
                .build();
    }

    @Test
    @DisplayName("MethodArgumentNotValidException 응답은 필드별 검증 오류와 요청 메타데이터를 포함한다")
    void methodArgumentNotValidExceptionContainsFieldErrorsAndRequestMetadata() throws Exception {
        mockMvc.perform(post("/test/validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "",
                                  "content": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("7000"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.path").value("/test/validation"))
                .andExpect(jsonPath("$.timestamp").value(matchesPattern(TIMESTAMP_PATTERN)))
                .andExpect(jsonPath("$.fieldErrors").isArray())
                .andExpect(jsonPath("$.fieldErrors[?(@.field == 'title')].message", contains("제목을 입력해주세요.")))
                .andExpect(jsonPath("$.fieldErrors[?(@.field == 'content')].message", contains("내용을 입력해주세요.")))
                .andExpect(jsonPath("$.message").value("입력값이 올바르지 않아요."));
    }

    @Test
    @DisplayName("ResponseStatusException reason이 사용자용 한글 메시지가 아니면 기본 메시지를 사용한다")
    void responseStatusExceptionWithNonKoreanReasonUsesDefaultMessage() throws Exception {
        ResultActions result = mockMvc.perform(post("/test/response-status")
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("4000"))
                .andExpect(jsonPath("$.message").value("요청을 처리하지 못했어요."));
        expectMetadata(result, 400, "/test/response-status");
    }

    @Test
    @DisplayName("ResponseStatusException reason이 사용자용 한글 메시지면 그대로 사용한다")
    void responseStatusExceptionWithKoreanReasonUsesReason() throws Exception {
        ResultActions result = mockMvc.perform(post("/test/response-status-korean")
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("4000"))
                .andExpect(jsonPath("$.message").value("이미 승인된 사용자예요."));
        expectMetadata(result, 400, "/test/response-status-korean");
    }

    @Test
    @DisplayName("ResponseStatusException reason이 없으면 기본 메시지를 사용한다")
    void responseStatusExceptionWithoutReasonUsesDefaultMessage() throws Exception {
        ResultActions result = mockMvc.perform(post("/test/response-status-without-reason")
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("4000"))
                .andExpect(jsonPath("$.message").value("요청을 처리하지 못했어요."));
        expectMetadata(result, 400, "/test/response-status-without-reason");
    }

    @Test
    @DisplayName("IllegalArgumentException 응답은 클라이언트 요청값 오류로 처리한다")
    void illegalArgumentExceptionReturnsBadRequest() throws Exception {
        ResultActions result = mockMvc.perform(post("/test/illegal-argument")
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("4001"))
                .andExpect(jsonPath("$.message").value("요청 값이 올바르지 않아요."));
        expectMetadata(result, 400, "/test/illegal-argument");
    }

    @Test
    @DisplayName("IllegalStateException 응답은 알 수 없는 서버 오류로 처리한다")
    void illegalStateExceptionReturnsUnknownServerError() throws Exception {
        ResultActions result = mockMvc.perform(post("/test/illegal-state")
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errorCode").value("0000"))
                .andExpect(jsonPath("$.message").value(UNKNOWN_MESSAGE));
        expectMetadata(result, 500, "/test/illegal-state");
    }

    @Test
    @DisplayName("잘못된 JSON 요청 본문은 클라이언트 요청 오류로 처리한다")
    void malformedJsonReturnsBadRequest() throws Exception {
        ResultActions result = mockMvc.perform(post("/test/validation")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{"));

        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("4001"))
                .andExpect(jsonPath("$.message").value("요청 본문 형식이 올바르지 않아요."));
        expectMetadata(result, 400, "/test/validation");
    }

    @Test
    @DisplayName("필수 요청 파라미터 누락은 클라이언트 요청 오류로 처리한다")
    void missingRequestParameterReturnsBadRequest() throws Exception {
        ResultActions result = mockMvc.perform(get("/api/event/test/year"));

        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("4001"))
                .andExpect(jsonPath("$.message").value("필수 요청 값이 누락됐어요."));
        expectMetadata(result, 400, "/api/event/test/year");
    }

    @Test
    @DisplayName("지원하지 않는 HTTP method는 405로 처리한다")
    void unsupportedMethodReturnsMethodNotAllowed() throws Exception {
        ResultActions result = mockMvc.perform(get("/test/response-status"));

        result.andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.errorCode").value("4050"))
                .andExpect(jsonPath("$.message").value("지원하지 않는 요청 방식이에요."));
        expectMetadata(result, 405, "/test/response-status");
    }

    @Test
    @DisplayName("지원하지 않는 media type은 415로 처리한다")
    void unsupportedMediaTypeReturnsUnsupportedMediaType() throws Exception {
        ResultActions result = mockMvc.perform(post("/test/media-type")
                .contentType(MediaType.TEXT_PLAIN)
                .content("plain"));

        result.andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.errorCode").value("4150"))
                .andExpect(jsonPath("$.message").value("지원하지 않는 요청 형식이에요."));
        expectMetadata(result, 415, "/test/media-type");
    }

    @Test
    @DisplayName("sort 값 오류는 한글 선택지 메시지와 요청 메타데이터를 포함한다")
    void sortExceptionReturnsFriendlyMessageAndMetadata() throws Exception {
        ResultActions result = mockMvc.perform(get("/api/event/test/sort")
                .param("sort", "BAD"));

        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("2200"))
                .andExpect(jsonPath("$.message").value("이벤트 탭을 예정 이벤트, 지난 이벤트, 나의 이벤트 중에서 선택해주세요."));
        expectMetadata(result, 400, "/api/event/test/sort");
    }

    @Test
    @DisplayName("type 파라미터 바인딩 오류는 이벤트 유형 선택지 메시지로 처리한다")
    void typeMismatchReturnsFriendlyMessage() throws Exception {
        ResultActions result = mockMvc.perform(get("/api/event/test/type")
                .param("type", "BAD"));

        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("2201"))
                .andExpect(jsonPath("$.message").value("이벤트 유형을 전체, 대회, 훈련 중에서 선택해주세요."));
        expectMetadata(result, 400, "/api/event/test/type");
    }

    @Test
    @DisplayName("kind 파라미터 바인딩 오류는 모집구분 선택지 메시지로 처리한다")
    void kindMismatchReturnsFriendlyMessage() throws Exception {
        ResultActions result = mockMvc.perform(get("/api/event/test/kind")
                .param("kind", "BAD"));

        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("2202"))
                .andExpect(jsonPath("$.message").value("모집구분을 전체, 모집중, 모집예정, 모집마감, 종료 중에서 선택해주세요."));
        expectMetadata(result, 400, "/api/event/test/kind");
    }

    @Test
    @DisplayName("year 파라미터 바인딩 오류는 연도 선택 메시지로 처리한다")
    void yearMismatchReturnsFriendlyMessage() throws Exception {
        ResultActions result = mockMvc.perform(get("/api/event/test/year")
                .param("year", "BAD"));

        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("2204"))
                .andExpect(jsonPath("$.message").value("연도 선택 값이 올바르지 않아요."));
        expectMetadata(result, 400, "/api/event/test/year");
    }

    @Test
    @DisplayName("month 파라미터 바인딩 오류는 월 선택 메시지로 처리한다")
    void monthMismatchReturnsFriendlyMessage() throws Exception {
        ResultActions result = mockMvc.perform(get("/api/event/test/month")
                .param("month", "BAD"));

        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("2205"))
                .andExpect(jsonPath("$.message").value("월 선택 값이 올바르지 않아요."));
        expectMetadata(result, 400, "/api/event/test/month");
    }

    @Test
    @DisplayName("day 파라미터 바인딩 오류는 일자 선택 메시지로 처리한다")
    void dayMismatchReturnsFriendlyMessage() throws Exception {
        ResultActions result = mockMvc.perform(get("/api/event/test/day")
                .param("day", "BAD"));

        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("2206"))
                .andExpect(jsonPath("$.message").value("일자 선택 값이 올바르지 않아요."));
        expectMetadata(result, 400, "/api/event/test/day");
    }

    @Test
    @DisplayName("이벤트 경로가 아닌 type 파라미터 바인딩 오류는 일반 요청값 오류로 처리한다")
    void nonEventTypeMismatchReturnsGenericMessage() throws Exception {
        ResultActions result = mockMvc.perform(get("/test/admin-type")
                .param("type", "BAD"));

        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("4001"))
                .andExpect(jsonPath("$.message").value("요청 값이 올바르지 않아요."));
        expectMetadata(result, 400, "/test/admin-type");
    }

    private static void expectMetadata(ResultActions result, int status, String path) throws Exception {
        result.andExpect(jsonPath("$.status").value(status))
                .andExpect(jsonPath("$.path").value(path))
                .andExpect(jsonPath("$.timestamp").value(matchesPattern(TIMESTAMP_PATTERN)));
    }

    private static MessageSource messageSource() {
        return new MessageConfig().messageSource("i18n/exception", "UTF-8");
    }

    @RestController
    private static class ValidationTestController {
        @PostMapping("/test/validation")
        void validate(@Valid @RequestBody ValidationRequest request) {
        }
    }

    private record ValidationRequest(
            @NotBlank(message = "제목을 입력해주세요.")
            String title,
            @NotBlank(message = "내용을 입력해주세요.")
            String content
    ) {
    }

    @RestController
    private static class CommonExceptionTestController {
        @PostMapping("/test/response-status")
        void responseStatus() {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId and role are required.");
        }

        @PostMapping("/test/response-status-without-reason")
        void responseStatusWithoutReason() {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        @PostMapping("/test/response-status-korean")
        void responseStatusKorean() {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 승인된 사용자예요.");
        }

        @PostMapping("/test/illegal-argument")
        void illegalArgument() {
            throw new IllegalArgumentException("bad request value");
        }

        @PostMapping("/test/illegal-state")
        void illegalState() {
            throw new IllegalStateException("illegal state");
        }

        @PostMapping(value = "/test/media-type", consumes = MediaType.APPLICATION_JSON_VALUE)
        void mediaType(@RequestBody ValidationRequest request) {
        }

        @GetMapping("/api/event/test/sort")
        void sort(@RequestParam("sort") String sort) {
            throw new NotValidSortException();
        }

        @GetMapping("/api/event/test/type")
        void type(@RequestParam("type") EventType type) {
        }

        @GetMapping("/api/event/test/kind")
        void kind(@RequestParam("kind") EventRecruitStatus kind) {
        }

        @GetMapping("/api/event/test/year")
        void year(@RequestParam("year") int year) {
        }

        @GetMapping("/api/event/test/month")
        void month(@RequestParam("month") int month) {
        }

        @GetMapping("/api/event/test/day")
        void day(@RequestParam("day") int day) {
        }

        @GetMapping("/test/admin-type")
        void adminType(@RequestParam("type") EventType type) {
        }
    }
}
