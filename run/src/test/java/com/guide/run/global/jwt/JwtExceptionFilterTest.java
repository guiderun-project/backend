package com.guide.run.global.jwt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guide.run.global.config.MessageConfig;
import com.guide.run.global.exception.auth.authorize.NotExistAuthorizationException;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class JwtExceptionFilterTest {

    @Test
    @DisplayName("JWT 필터의 인증 정보 없음 예외는 YAML과 같은 code/message로 401 응답한다")
    void notExistAuthorizationExceptionReturnsYamlCodeAndMessage() throws Exception {
        MessageSource messageSource = new MessageConfig().messageSource("i18n/exception", "UTF-8");
        JwtExceptionFilter filter = new JwtExceptionFilter();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = (servletRequest, servletResponse) -> {
            throw new NotExistAuthorizationException();
        };

        filter.doFilter(request, response, chain);

        JsonNode body = new ObjectMapper().readTree(response.getContentAsString(StandardCharsets.UTF_8));
        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(body.get("errorCode").asText())
                .isEqualTo(messageSource.getMessage("notExistAuthorization.code", null, Locale.KOREAN));
        assertThat(body.get("message").asText())
                .isEqualTo(messageSource.getMessage("notExistAuthorization.msg", null, Locale.KOREAN));
    }
}
