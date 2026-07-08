package com.guide.run.global.exception;

import com.guide.run.global.exception.validation.ValidationExceptionAdvice;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionAdviceTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new ValidationTestController())
                .setControllerAdvice(new ValidationExceptionAdvice(new ErrorResponseFactory()))
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
                .andExpect(jsonPath("$.timestamp").value(matchesPattern(
                        "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(?:\\.\\d+)?\\+09:00$"
                )))
                .andExpect(jsonPath("$.fieldErrors").isArray())
                .andExpect(jsonPath("$.fieldErrors[?(@.field == 'title')].message", contains("제목을 입력해주세요.")))
                .andExpect(jsonPath("$.fieldErrors[?(@.field == 'content')].message", contains("내용을 입력해주세요.")))
                .andExpect(jsonPath("$.message").value("입력값이 올바르지 않아요."));
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
}
