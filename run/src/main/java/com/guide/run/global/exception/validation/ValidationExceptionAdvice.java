package com.guide.run.global.exception.validation;

import com.guide.run.global.dto.response.ValidFailResult;
import com.guide.run.global.exception.ErrorResponseFactory;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RequiredArgsConstructor
@Order(Ordered.LOWEST_PRECEDENCE - 1)
@RestControllerAdvice
public class ValidationExceptionAdvice {
    private final ErrorResponseFactory errorResponseFactory;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ValidFailResult> handleValidationException(
            MethodArgumentNotValidException e,
            HttpServletRequest request
    ) {
        return ResponseEntity.badRequest().body(errorResponseFactory.validation(
                "7000",
                "입력값이 올바르지 않아요.",
                e.getBindingResult().getFieldErrors(),
                request
        ));
    }
}
