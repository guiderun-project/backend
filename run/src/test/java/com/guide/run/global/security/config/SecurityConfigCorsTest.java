package com.guide.run.global.security.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityConfigCorsTest {

    private CorsConfiguration corsConfigFor(String configuredOrigins) {
        SecurityConfig securityConfig = new SecurityConfig(null, null);
        ReflectionTestUtils.setField(securityConfig, "origin", configuredOrigins);

        UrlBasedCorsConfigurationSource source =
                (UrlBasedCorsConfigurationSource) securityConfig.corsConfigurationSource();
        return source.getCorsConfigurations().get("/**");
    }

    @Test
    @DisplayName("운영 CORS는 설정된 운영 오리진만 신뢰하고 dev 오리진은 신뢰하지 않는다")
    void prodTrustsOnlyConfiguredProdOrigins() {
        CorsConfiguration config = corsConfigFor("https://guiderun.org,https://www.guiderun.org");

        assertThat(config.getAllowedOrigins())
                .containsExactlyInAnyOrder("https://guiderun.org", "https://www.guiderun.org");
        assertThat(config.getAllowedOrigins()).doesNotContain("https://dev.guiderun.org");
    }

    @Test
    @DisplayName("dev CORS는 dev 오리진만 신뢰하고 운영 오리진을 상속하지 않는다")
    void devTrustsOnlyDevOrigin() {
        CorsConfiguration config = corsConfigFor("https://dev.guiderun.org");

        assertThat(config.getAllowedOrigins()).containsExactly("https://dev.guiderun.org");
    }
}
