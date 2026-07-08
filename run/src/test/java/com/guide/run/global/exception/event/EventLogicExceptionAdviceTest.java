package com.guide.run.global.exception.event;

import com.guide.run.global.config.MessageConfig;
import com.guide.run.global.dto.response.FailResult;
import com.guide.run.global.exception.event.logic.EventValidationException;
import com.guide.run.global.exception.event.logic.NotValidDayException;
import com.guide.run.global.exception.event.logic.NotValidKindException;
import com.guide.run.global.exception.event.logic.NotValidMonthException;
import com.guide.run.global.exception.event.logic.NotValidSortException;
import com.guide.run.global.exception.event.logic.NotValidTypeException;
import com.guide.run.global.exception.event.logic.NotValidYearException;
import com.guide.run.global.service.ResponseService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;
import java.util.stream.Stream;

class EventLogicExceptionAdviceTest {

    @AfterEach
    void resetLocale() {
        LocaleContextHolder.resetLocaleContext();
    }

    @Test
    @DisplayName("이벤트 검증 예외는 400 실패 응답으로 변환한다")
    void eventValidationExceptionReturnsBadRequest() {
        EventLogicExceptionAdvice advice = advice();

        ResponseEntity<FailResult> response = advice.EventValidationException(
                new EventValidationException("대회 신청 정보는 필수입니다.")
        );

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo("2210");
        assertThat(response.getBody().getMessage()).isEqualTo("대회 신청 정보는 필수입니다.");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("eventFilterExceptions")
    @DisplayName("이벤트 필터 예외는 사용자 안내 메시지로 변환한다")
    void eventFilterExceptionsReturnFriendlyMessages(
            String name,
            EventAdviceCall call,
            String expectedCode,
            String expectedMessage
    ) {
        ResponseEntity<FailResult> response = call.invoke(advice());

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo(expectedCode);
        assertThat(response.getBody().getMessage()).isEqualTo(expectedMessage);
    }

    private static Stream<Arguments> eventFilterExceptions() {
        return Stream.of(
                Arguments.of(
                        "notValidSort",
                        (EventAdviceCall) advice -> advice.NotValidSortException(new NotValidSortException()),
                        "2200",
                        "이벤트 탭을 예정 이벤트, 지난 이벤트, 나의 이벤트 중에서 선택해주세요."
                ),
                Arguments.of(
                        "notValidType",
                        (EventAdviceCall) advice -> advice.NotValidTypeException(new NotValidTypeException()),
                        "2201",
                        "이벤트 유형을 전체, 대회, 훈련 중에서 선택해주세요."
                ),
                Arguments.of(
                        "notValidKind",
                        (EventAdviceCall) advice -> advice.NotValidKindException(new NotValidKindException()),
                        "2202",
                        "모집구분을 전체, 모집중, 모집예정, 모집마감, 종료 중에서 선택해주세요."
                ),
                Arguments.of(
                        "notValidYear",
                        (EventAdviceCall) advice -> advice.NotValidYearException(new NotValidYearException()),
                        "2204",
                        "연도 선택 값이 올바르지 않아요."
                ),
                Arguments.of(
                        "notValidMonth",
                        (EventAdviceCall) advice -> advice.NotValidMonthException(new NotValidMonthException()),
                        "2205",
                        "월 선택 값이 올바르지 않아요."
                ),
                Arguments.of(
                        "notValidDay",
                        (EventAdviceCall) advice -> advice.NotValidDayException(new NotValidDayException()),
                        "2206",
                        "일자 선택 값이 올바르지 않아요."
                )
        );
    }

    private static EventLogicExceptionAdvice advice() {
        LocaleContextHolder.setLocale(Locale.KOREAN);
        MessageSource messageSource = new MessageConfig().messageSource("i18n/exception", "UTF-8");
        return new EventLogicExceptionAdvice(messageSource, new ResponseService());
    }

    private interface EventAdviceCall {
        ResponseEntity<FailResult> invoke(EventLogicExceptionAdvice advice);
    }
}
