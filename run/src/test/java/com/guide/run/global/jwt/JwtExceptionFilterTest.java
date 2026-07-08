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
    private static final String TIMESTAMP_PATTERN = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(?:\\.\\d+)?\\+09:00$";

    @Test
    @DisplayName("JWT 필터의 인증 정보 없음 예외는 YAML과 같은 code/message와 요청 메타데이터로 401 응답한다")
    void notExistAuthorizationExceptionReturnsYamlCodeAndMessage() throws Exception {
        MessageSource messageSource = new MessageConfig().messageSource("i18n/exception", "UTF-8");
        JwtExceptionFilter filter = new JwtExceptionFilter();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/user/personal");
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
        assertThat(body.get("status").asInt()).isEqualTo(401);
        assertThat(body.get("path").asText()).isEqualTo("/api/user/personal");
        assertThat(body.get("timestamp").asText()).matches(TIMESTAMP_PATTERN);
    }
}
