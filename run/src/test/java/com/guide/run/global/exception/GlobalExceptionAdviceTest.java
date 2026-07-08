package com.guide.run.global.exception;

import com.guide.run.global.exception.validation.ValidationExceptionAdvice;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.matchesPattern;
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
        mockMvc = MockMvcBuilders
                .standaloneSetup(new ValidationTestController(), new CommonExceptionTestController())
                .setControllerAdvice(
                        new ValidationExceptionAdvice(errorResponseFactory),
                        new GlobalExceptionAdvice(errorResponseFactory),
                        new UnknownExceptionAdvice(messageSource(), errorResponseFactory)
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
    @DisplayName("ResponseStatusException 응답은 실제 상태 코드와 reason 메시지를 사용한다")
    void responseStatusExceptionUsesStatusAndReason() throws Exception {
        ResultActions result = mockMvc.perform(post("/test/response-status")
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("4000"))
                .andExpect(jsonPath("$.message").value("userId and role are required."));
        expectMetadata(result, 400, "/test/response-status");
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

    private static void expectMetadata(ResultActions result, int status, String path) throws Exception {
        result.andExpect(jsonPath("$.status").value(status))
                .andExpect(jsonPath("$.path").value(path))
                .andExpect(jsonPath("$.timestamp").value(matchesPattern(TIMESTAMP_PATTERN)));
    }

    private static StaticMessageSource messageSource() {
        StaticMessageSource messageSource = new StaticMessageSource();
        messageSource.addMessage("unknown.code", Locale.KOREA, "0000");
        messageSource.addMessage("unknown.msg", Locale.KOREA, UNKNOWN_MESSAGE);
        messageSource.addMessage("unknown.code", Locale.getDefault(), "0000");
        messageSource.addMessage("unknown.msg", Locale.getDefault(), UNKNOWN_MESSAGE);
        messageSource.addMessage("unknown.code", Locale.ENGLISH, "0000");
        messageSource.addMessage("unknown.msg", Locale.ENGLISH, UNKNOWN_MESSAGE);
        return messageSource;
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

        @PostMapping("/test/illegal-argument")
        void illegalArgument() {
            throw new IllegalArgumentException("bad request value");
        }

        @PostMapping("/test/illegal-state")
        void illegalState() {
            throw new IllegalStateException("illegal state");
        }
    }
}
