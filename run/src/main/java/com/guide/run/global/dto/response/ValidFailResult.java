package com.guide.run.global.dto.response;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

@Data
@Builder
public class ValidFailResult {
    private String errorCode;
    private String message;

    public static ResponseEntity<ValidFailResult> toResult(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        FieldError fieldError = fieldErrors.get(fieldErrors.size()-1);  // 가장 첫 번째 에러 필드
        String fieldName = fieldError.getField();   // 필드명

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ValidFailResult.builder()
                        .errorCode("7000")
                        .message(fieldName + " 필드의 입력값이 유효하지 않습니다.")
                        .build());
    }
}
