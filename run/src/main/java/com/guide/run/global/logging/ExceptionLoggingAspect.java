package com.guide.run.global.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * 모든 @ExceptionHandler 메서드 실행 시 자동으로 로그를 남기는 AOP.
 * 각 ExceptionAdvice 클래스를 수정하지 않고 한 곳에서 예외 로그를 관리한다.
 *
 * - 4xx (클라이언트 오류): WARN 레벨
 * - 5xx (서버 오류): ERROR 레벨 + 스택 트레이스
 */
@Slf4j
@Aspect
@Component
public class ExceptionLoggingAspect {

    /** @ExceptionHandler 메서드 진입 시 예외 정보를 로깅 */
    @Before("@annotation(org.springframework.web.bind.annotation.ExceptionHandler)")
    public void logBeforeExceptionHandler(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length == 0) return;

        // 첫 번째 인자가 예외 객체인 경우 로그 출력
        for (Object arg : args) {
            if (arg instanceof Exception ex) {
                String handlerName = joinPoint.getSignature().getName();
                String exceptionClass = ex.getClass().getSimpleName();
                String message = ex.getMessage();

                // 예외 메시지가 없는 경우 기본값
                if (message == null || message.isBlank()) {
                    message = "(메시지 없음)";
                }

                log.warn("[EXCEPTION] handler={} | type={} | msg={}", handlerName, exceptionClass, message);
                return;
            }
        }
    }

    /** @ExceptionHandler 메서드 반환 후 HTTP 상태 코드 기반으로 레벨 재분류 */
    @AfterReturning(
            pointcut = "@annotation(org.springframework.web.bind.annotation.ExceptionHandler)",
            returning = "result"
    )
    public void logAfterExceptionHandler(JoinPoint joinPoint, Object result) {
        if (!(result instanceof ResponseEntity<?> responseEntity)) return;

        int status = responseEntity.getStatusCode().value();
        Object[] args = joinPoint.getArgs();
        Exception cause = null;
        for (Object arg : args) {
            if (arg instanceof Exception ex) {
                cause = ex;
                break;
            }
        }

        String handlerName = joinPoint.getSignature().getName();

        if (status >= 500) {
            log.error("[EXCEPTION-RESULT] handler={} | STATUS={} | {}",
                    handlerName, status, cause != null ? cause.getClass().getSimpleName() : "unknown", cause);
        }
        // 4xx는 @Before에서 이미 WARN 로그가 찍히므로 중복 방지를 위해 생략
    }
}
