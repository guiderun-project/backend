package com.guide.run.global.exception;

import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.event.entity.type.EventType;
import com.guide.run.global.dto.response.FailResult;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.util.regex.Pattern;

@RequiredArgsConstructor
@RestControllerAdvice
@Order(0)
public class GlobalExceptionAdvice {
    private static final Pattern KOREAN_PATTERN = Pattern.compile("[가-힣]");
    private static final String DEFAULT_RESPONSE_STATUS_MESSAGE = "요청을 처리하지 못했어요.";
    private static final String ILLEGAL_ARGUMENT_MESSAGE = "요청 값이 올바르지 않아요.";
    private static final String MALFORMED_BODY_MESSAGE = "요청 본문 형식이 올바르지 않아요.";
    private static final String MISSING_REQUEST_VALUE_MESSAGE = "필수 요청 값이 누락됐어요.";
    private static final String METHOD_NOT_SUPPORTED_MESSAGE = "지원하지 않는 요청 방식이에요.";
    private static final String MEDIA_TYPE_NOT_SUPPORTED_MESSAGE = "지원하지 않는 요청 형식이에요.";

    private final ErrorResponseFactory errorResponseFactory;
    private final MessageSource messageSource;

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

    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<FailResult> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException e,
            HttpServletRequest request
    ) {
        return badRequest("4001", MALFORMED_BODY_MESSAGE, request);
    }

    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            ServletRequestBindingException.class
    })
    protected ResponseEntity<FailResult> handleMissingRequestValueException(
            Exception e,
            HttpServletRequest request
    ) {
        return badRequest("4001", MISSING_REQUEST_VALUE_MESSAGE, request);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<FailResult> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e,
            HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(errorResponseFactory.fail(
                "4050",
                METHOD_NOT_SUPPORTED_MESSAGE,
                HttpStatus.METHOD_NOT_ALLOWED,
                request
        ));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    protected ResponseEntity<FailResult> handleHttpMediaTypeNotSupportedException(
            HttpMediaTypeNotSupportedException e,
            HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(errorResponseFactory.fail(
                "4150",
                MEDIA_TYPE_NOT_SUPPORTED_MESSAGE,
                HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                request
        ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<FailResult> handleIllegalArgumentException(
            IllegalArgumentException e,
            HttpServletRequest request
    ) {
        return badRequest("4001", ILLEGAL_ARGUMENT_MESSAGE, request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<FailResult> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e,
            HttpServletRequest request
    ) {
        ErrorMessage errorMessage = resolveTypeMismatchError(e, request);

        return badRequest(
                errorMessage.code(),
                errorMessage.message(),
                request
        );
    }

    private String resolveReason(ResponseStatusException e) {
        if (e.getReason() == null || e.getReason().isBlank() || !containsKorean(e.getReason())) {
            return DEFAULT_RESPONSE_STATUS_MESSAGE;
        }
        return e.getReason();
    }

    private ErrorMessage resolveTypeMismatchError(
            MethodArgumentTypeMismatchException e,
            HttpServletRequest request
    ) {
        String parameterName = e.getName();
        if (parameterName == null) {
            return new ErrorMessage("4001", ILLEGAL_ARGUMENT_MESSAGE);
        }

        if (!isEventRequest(request)) {
            return new ErrorMessage("4001", ILLEGAL_ARGUMENT_MESSAGE);
        }

        Class<?> requiredType = e.getRequiredType();
        if ("type".equals(parameterName) && EventType.class.equals(requiredType)) {
            return errorMessage("notValidType");
        }
        if (("kind".equals(parameterName) || "recruitStatus".equals(parameterName))
                && EventRecruitStatus.class.equals(requiredType)) {
            return errorMessage("notValidKind");
        }

        return switch (parameterName) {
            case "tab", "sort" -> errorMessage("notValidSort");
            case "year" -> errorMessage("notValidYear");
            case "month" -> errorMessage("notValidMonth");
            case "day" -> errorMessage("notValidDay");
            default -> new ErrorMessage("4001", ILLEGAL_ARGUMENT_MESSAGE);
        };
    }

    private ResponseEntity<FailResult> badRequest(String errorCode, String message, HttpServletRequest request) {
        return ResponseEntity.badRequest().body(errorResponseFactory.fail(
                errorCode,
                message,
                HttpStatus.BAD_REQUEST,
                request
        ));
    }

    private boolean containsKorean(String reason) {
        return KOREAN_PATTERN.matcher(reason).find();
    }

    private boolean isEventRequest(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/api/event/");
    }

    private ErrorMessage errorMessage(String key) {
        return new ErrorMessage(
                getMessage(key + ".code"),
                getMessage(key + ".msg")
        );
    }

    private String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }

    private record ErrorMessage(String code, String message) {
    }
}
