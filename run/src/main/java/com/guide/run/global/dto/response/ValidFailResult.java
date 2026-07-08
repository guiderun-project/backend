package com.guide.run.global.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ValidFailResult {
    private String errorCode;
    private String message;
    private Integer status;
    private String path;
    private String timestamp;
    private List<FieldErrorResult> fieldErrors;
}
