package com.guide.run.global.jwt;

import com.guide.run.global.exception.auth.authorize.NotValidAccessTokenException;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class JwtAuthenticationFilterTest {

    @Test
    @DisplayName("Authorization 헤더가 없으면 JWT 검증 없이 다음 필터로 진행한다")
    void doFilterSkipsJwtProviderWhenAuthorizationHeaderIsMissing() throws ServletException, IOException {
        JwtProvider jwtProvider = mock(JwtProvider.class);
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtProvider);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        verifyNoInteractions(jwtProvider);
    }

    @Test
    @DisplayName("Authorization 헤더가 Bearer 형식이 아니면 인증 실패로 처리한다")
    void doFilterRejectsMalformedAuthorizationHeader() {
        JwtProvider jwtProvider = mock(JwtProvider.class);
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtProvider);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Basic abc");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        assertThatThrownBy(() -> filter.doFilter(request, response, chain))
                .isInstanceOf(NotValidAccessTokenException.class);
        verifyNoInteractions(jwtProvider);
    }
}
