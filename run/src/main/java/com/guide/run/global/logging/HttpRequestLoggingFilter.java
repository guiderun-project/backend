package com.guide.run.global.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * 모든 HTTP 요청과 응답을 로그로 기록하는 필터.
 * MDC(Mapped Diagnostic Context)를 이용해 요청별 고유 ID, URI, IP, 메서드를 모든 로그에 포함시킴.
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class HttpRequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestId = UUID.randomUUID().toString().substring(0, 8);
        String uri = request.getRequestURI();
        String method = request.getMethod();
        String clientIp = getClientIp(request);
        String queryString = request.getQueryString();
        String fullUri = queryString != null ? uri + "?" + queryString : uri;

        // MDC에 요청 정보 등록 → 이후 모든 로그에 자동 포함
        MDC.put("requestId", requestId);
        MDC.put("requestURI", fullUri);
        MDC.put("clientIp", clientIp);

        long startTime = System.currentTimeMillis();

        try {
            log.info("[REQ] [{}] {} {} | IP={}", requestId, method, fullUri, clientIp);
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            int status = response.getStatus();
            String logLevel = resolveLogLevel(status);

            if ("ERROR".equals(logLevel)) {
                log.error("[RES] [{}] {} {} | STATUS={} | {}ms", requestId, method, fullUri, status, duration);
            } else if ("WARN".equals(logLevel)) {
                log.warn("[RES] [{}] {} {} | STATUS={} | {}ms", requestId, method, fullUri, status, duration);
            } else {
                log.info("[RES] [{}] {} {} | STATUS={} | {}ms", requestId, method, fullUri, status, duration);
            }

            MDC.clear();
        }
    }

    /** 상태 코드에 따라 로그 레벨 결정 */
    private String resolveLogLevel(int status) {
        if (status >= 500) return "ERROR";
        if (status >= 400) return "WARN";
        return "INFO";
    }

    /** 프록시 환경을 고려한 실제 클라이언트 IP 추출 */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip.split(",")[0].trim();
        }
        ip = request.getHeader("X-Real-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        return request.getRemoteAddr();
    }

    /** 정적 리소스, Swagger는 로그에서 제외 */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.startsWith("/swagger-ui")
                || uri.startsWith("/v3/api-docs")
                || uri.startsWith("/actuator")
                || uri.endsWith(".ico")
                || uri.endsWith(".css")
                || uri.endsWith(".js");
    }
}
