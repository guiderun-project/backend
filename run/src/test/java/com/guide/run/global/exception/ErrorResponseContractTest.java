package com.guide.run.global.exception;

import com.guide.run.global.dto.response.FailResult;
import com.guide.run.global.dto.response.FieldErrorResult;
import com.guide.run.global.dto.response.ValidFailResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorResponseContractTest {

    @Test
    @DisplayName("FailResult는 기존 필드와 확장 필드를 함께 가진다")
    void failResultContainsBaseAndExtendedFields() {
        FailResult result = new FailResult(
                "2200",
                "이벤트 탭을 예정 이벤트, 지난 이벤트, 나의 이벤트 중에서 선택해주세요.",
                400,
                "/api/event/all",
                "2026-07-09T10:20:30+09:00"
        );

        assertThat(result.getErrorCode()).isEqualTo("2200");
        assertThat(result.getMessage()).contains("이벤트 탭");
        assertThat(result.getStatus()).isEqualTo(400);
        assertThat(result.getPath()).isEqualTo("/api/event/all");
        assertThat(result.getTimestamp()).isEqualTo("2026-07-09T10:20:30+09:00");
    }

    @Test
    @DisplayName("FailResult는 기존 2개 인자 생성자를 유지한다")
    void failResultKeepsLegacyConstructor() {
        FailResult result = new FailResult("2200", "요청 값이 올바르지 않습니다.");

        assertThat(result.getErrorCode()).isEqualTo("2200");
        assertThat(result.getMessage()).isEqualTo("요청 값이 올바르지 않습니다.");
        assertThat(result.getStatus()).isNull();
        assertThat(result.getPath()).isNull();
        assertThat(result.getTimestamp()).isNull();
    }

    @Test
    @DisplayName("ValidFailResult는 확장 필드와 fieldErrors를 포함한다")
    void validFailResultContainsExtendedFieldsAndFieldErrors() {
        ValidFailResult result = ValidFailResult.builder()
                .errorCode("7000")
                .message("입력값이 올바르지 않아요.")
                .status(400)
                .path("/api/event")
                .timestamp("2026-07-09T10:20:30+09:00")
                .fieldErrors(List.of(new FieldErrorResult(
                        "eventContent",
                        "모임 내용은 500자 이하로 입력해주세요."
                )))
                .build();

        assertThat(result.getErrorCode()).isEqualTo("7000");
        assertThat(result.getMessage()).isEqualTo("입력값이 올바르지 않아요.");
        assertThat(result.getStatus()).isEqualTo(400);
        assertThat(result.getPath()).isEqualTo("/api/event");
        assertThat(result.getTimestamp()).isEqualTo("2026-07-09T10:20:30+09:00");
        assertThat(result.getFieldErrors())
                .extracting(FieldErrorResult::getField)
                .containsExactly("eventContent");
    }
}
