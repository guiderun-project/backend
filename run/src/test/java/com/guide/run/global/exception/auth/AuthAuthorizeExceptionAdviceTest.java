package com.guide.run.global.exception.auth;

import com.guide.run.global.config.MessageConfig;
import com.guide.run.global.dto.response.FailResult;
import com.guide.run.global.exception.auth.authorize.NotExistAuthorizationException;
import com.guide.run.global.service.ResponseService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class AuthAuthorizeExceptionAdviceTest {

    @AfterEach
    void resetLocale() {
        LocaleContextHolder.resetLocaleContext();
    }

    @Test
    @DisplayName("인증 정보를 찾을 수 없는 예외는 401 실패 응답으로 변환한다")
    void notExistAuthorizationExceptionReturnsUnauthorized() {
        AuthAuthorizeExceptionAdvice advice = advice();

        ResponseEntity<FailResult> response = advice.NotExistAuthorizationException(new NotExistAuthorizationException());

        assertThat(response.getStatusCode().value()).isEqualTo(401);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo("0101");
        assertThat(response.getBody().getMessage()).isEqualTo("인증할 수 있는 사용자 데이터가 존재하지 않습니다");
    }

    private static AuthAuthorizeExceptionAdvice advice() {
        LocaleContextHolder.setLocale(Locale.KOREAN);
        MessageSource messageSource = new MessageConfig().messageSource("i18n/exception", "UTF-8");
        return new AuthAuthorizeExceptionAdvice(messageSource, new ResponseService());
    }
}
