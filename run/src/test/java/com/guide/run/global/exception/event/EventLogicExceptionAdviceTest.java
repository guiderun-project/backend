package com.guide.run.global.exception.event;

import com.guide.run.global.dto.response.FailResult;
import com.guide.run.global.exception.event.logic.EventValidationException;
import com.guide.run.global.service.ResponseService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EventLogicExceptionAdviceTest {

    @Test
    @DisplayName("이벤트 검증 예외는 400 실패 응답으로 변환한다")
    void eventValidationExceptionReturnsBadRequest() {
        MessageSource messageSource = mock(MessageSource.class);
        EventLogicExceptionAdvice advice = new EventLogicExceptionAdvice(messageSource, new ResponseService());
        when(messageSource.getMessage(eq("EventValidation.code"), isNull(), any()))
                .thenReturn("2210");

        ResponseEntity<FailResult> response = advice.EventValidationException(
                new EventValidationException("대회 신청 정보는 필수입니다.")
        );

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo("2210");
        assertThat(response.getBody().getMessage()).isEqualTo("대회 신청 정보는 필수입니다.");
    }
}
