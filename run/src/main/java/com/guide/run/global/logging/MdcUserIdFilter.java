package com.guide.run.global.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Spring Security 인증 후 MDC에 사용자 ID를 추가하는 필터.
 * 에러 로그에 어떤 유저가 발생시킨 오류인지 기록된다.
 */
@Slf4j
@Component
@Order(10)
public class MdcUserIdFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
                MDC.put("userId", auth.getName());
            }
        } catch (Exception ignored) {
            // MDC 설정 실패는 무시 (로깅 실패가 비즈니스 로직에 영향 주지 않도록)
        }
        filterChain.doFilter(request, response);
    }
}
