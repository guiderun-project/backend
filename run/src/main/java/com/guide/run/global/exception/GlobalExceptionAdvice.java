package com.guide.run.global.exception;

import com.guide.run.global.dto.response.FailResult;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@RestControllerAdvice
@Order(0)
public class GlobalExceptionAdvice {
    private static final String DEFAULT_RESPONSE_STATUS_MESSAGE = "요청을 처리하지 못했어요.";
    private static final String ILLEGAL_ARGUMENT_MESSAGE = "요청 값이 올바르지 않아요.";

    private final ErrorResponseFactory errorResponseFactory;

    @ExceptionHandler(ResponseStatusException.class)
    protected ResponseEntity<FailResult> handleResponseStatusException(
            ResponseStatusException e,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.valueOf(e.getStatusCode().value());

        return ResponseEntity.status(status).body(errorResponseFactory.fail(
                String.valueOf(status.value() * 10),
                resolveReason(e),
                status,
                request
        ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<FailResult> handleIllegalArgumentException(
            IllegalArgumentException e,
            HttpServletRequest request
    ) {
        return ResponseEntity.badRequest().body(errorResponseFactory.fail(
                "4001",
                ILLEGAL_ARGUMENT_MESSAGE,
                HttpStatus.BAD_REQUEST,
                request
        ));
    }

    private String resolveReason(ResponseStatusException e) {
        if (e.getReason() == null || e.getReason().isBlank()) {
            return DEFAULT_RESPONSE_STATUS_MESSAGE;
        }
        return e.getReason();
    }
}
