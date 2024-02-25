package com.guide.run.global.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
public class FailResult {
    private String errorCode;
    private String message;
}
