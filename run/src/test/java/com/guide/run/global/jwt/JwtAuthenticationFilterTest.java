package com.guide.run.global.jwt;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

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
}
