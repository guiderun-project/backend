package com.guide.run.global.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "실패 응답")
public class FailResult {
    @Schema(description = "에러 코드", example = "BAD_REQUEST")
    private String errorCode;
    @Schema(description = "에러 메시지", example = "요청 값이 올바르지 않습니다.")
    private String message;
}
