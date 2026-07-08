package com.guide.run.global.security.config;

import com.guide.run.global.exception.ErrorResponseFactory;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityConfigCorsTest {

    private static final List<String> REQUIRED_ORIGINS = List.of(
            "https://dev.guiderun.org",
            "https://guiderun.org",
            "https://www.guiderun.org"
    );

    @Test
    @DisplayName("전역 CORS 설정은 dev/prod/www 도메인을 항상 허용한다")
    void globalCorsAllowsRequiredOrigins() {
        SecurityConfig securityConfig = new SecurityConfig(null, new ErrorResponseFactory());
        ReflectionTestUtils.setField(securityConfig, "origin", "http://localhost:3000");

        CorsConfiguration corsConfiguration = corsConfiguration(securityConfig, "/api/event/summary");

        assertThat(corsConfiguration.getAllowedOrigins()).containsAll(REQUIRED_ORIGINS);
        assertThat(corsConfiguration.getAllowCredentials()).isTrue();
        assertThat(REQUIRED_ORIGINS).allSatisfy(origin ->
                assertThat(corsConfiguration.checkOrigin(origin)).isEqualTo(origin)
        );
    }

    @Test
    @DisplayName("전역 CORS 설정은 환경변수로 주입한 origin도 함께 허용한다")
    void globalCorsAllowsConfiguredOrigin() {
        SecurityConfig securityConfig = new SecurityConfig(null, new ErrorResponseFactory());
        ReflectionTestUtils.setField(securityConfig, "origin", "http://localhost:3000");

        CorsConfiguration corsConfiguration = corsConfiguration(securityConfig, "/api/user/mypage");

        assertThat(corsConfiguration.checkOrigin("http://localhost:3000")).isEqualTo("http://localhost:3000");
    }

    private CorsConfiguration corsConfiguration(SecurityConfig securityConfig, String requestUri) {
        CorsConfigurationSource source = securityConfig.corsConfigurationSource();
        HttpServletRequest request = new MockHttpServletRequest("OPTIONS", requestUri);
        CorsConfiguration corsConfiguration = source.getCorsConfiguration(request);
        assertThat(corsConfiguration).isNotNull();
        return corsConfiguration;
    }
}
