package com.guide.run.global.exception;

import com.guide.run.global.dto.response.FailResult;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.time.OffsetDateTime;
import java.time.ZoneId;

@RestControllerAdvice
public class FailResultResponseBodyAdvice implements ResponseBodyAdvice<Object> {
    private static final ZoneId SEOUL_ZONE = ZoneId.of("Asia/Seoul");

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response
    ) {
        if (!(body instanceof FailResult failResult)) {
            return body;
        }

        if (failResult.getStatus() != null && failResult.getPath() != null && failResult.getTimestamp() != null) {
            return failResult;
        }

        return new FailResult(
                failResult.getErrorCode(),
                failResult.getMessage(),
                resolveStatus(response),
                request.getURI().getPath(),
                OffsetDateTime.now(SEOUL_ZONE).toString()
        );
    }

    private int resolveStatus(ServerHttpResponse response) {
        if (response instanceof ServletServerHttpResponse servletResponse) {
            return servletResponse.getServletResponse().getStatus();
        }
        return 500;
    }
}
