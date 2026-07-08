package com.guide.run.global.exception;

import com.guide.run.global.dto.response.FailResult;
import com.guide.run.global.dto.response.FieldErrorResult;
import com.guide.run.global.dto.response.ValidFailResult;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

@Component
public class ErrorResponseFactory {
    private static final ZoneId SEOUL_ZONE = ZoneId.of("Asia/Seoul");

    public FailResult fail(String errorCode, String message, HttpStatus status, HttpServletRequest request) {
        return new FailResult(
                errorCode,
                message,
                status.value(),
                request.getRequestURI(),
                timestamp()
        );
    }

    public ValidFailResult validation(
            String errorCode,
            String message,
            List<FieldError> fieldErrors,
            HttpServletRequest request
    ) {
        List<FieldErrorResult> results = fieldErrors.stream()
                .map(fieldError -> new FieldErrorResult(fieldError.getField(), resolveFieldMessage(fieldError)))
                .toList();

        return ValidFailResult.builder()
                .errorCode(errorCode)
                .message(message)
                .status(HttpStatus.BAD_REQUEST.value())
                .path(request.getRequestURI())
                .timestamp(timestamp())
                .fieldErrors(results)
                .build();
    }

    private String timestamp() {
        return OffsetDateTime.now(SEOUL_ZONE).toString();
    }

    private String resolveFieldMessage(FieldError fieldError) {
        String defaultMessage = fieldError.getDefaultMessage();
        if (defaultMessage == null || defaultMessage.isBlank()) {
            return fieldError.getField() + " 입력값이 올바르지 않아요.";
        }
        return defaultMessage;
    }
}
