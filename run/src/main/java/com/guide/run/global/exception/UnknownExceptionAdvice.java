package com.guide.run.global.exception;

import com.guide.run.global.dto.response.FailResult;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
@Order(Integer.MAX_VALUE)
public class UnknownExceptionAdvice {
    private final MessageSource messageSource;
    private final ErrorResponseFactory errorResponseFactory;

    //0000 - 명시적으로 정의된 알 수 없는 예외
    @ExceptionHandler(UnknownException.class)
    protected ResponseEntity<FailResult> handleUnknownException(UnknownException e, HttpServletRequest request) {
        log.error("[서버 오류] UnknownException 발생 - msg={}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponseFactory.fail(
                getMessage("unknown.code"),
                getMessage("unknown.msg"),
                HttpStatus.INTERNAL_SERVER_ERROR,
                request
        ));
    }

    /**
     * 처리되지 않은 모든 예외에 대한 최후 방어선.
     * 특정 ExceptionAdvice에서 처리하지 못한 예외가 여기서 잡힌다.
     * 반드시 스택 트레이스 전체를 로그에 남긴다.
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<FailResult> handleUnexpectedException(Exception e, HttpServletRequest request) {
        log.error("[처리되지 않은 예외] type={} | msg={}", e.getClass().getName(), e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponseFactory.fail(
                getMessage("unknown.code"),
                getMessage("unknown.msg"),
                HttpStatus.INTERNAL_SERVER_ERROR,
                request
        ));
    }

    private String getMessage(String code) {
        return getMessage(code, null);
    }

    private String getMessage(String code, Object[] args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }
}
