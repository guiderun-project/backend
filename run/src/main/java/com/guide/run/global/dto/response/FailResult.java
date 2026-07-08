package com.guide.run.global.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "실패 응답")
public class FailResult {
    @Schema(description = "에러 코드", example = "BAD_REQUEST")
    private String errorCode;
    @Schema(description = "에러 메시지", example = "요청 값이 올바르지 않습니다.")
    private String message;
    @Schema(description = "HTTP 상태 코드", example = "400")
    private Integer status;
    @Schema(description = "요청 경로", example = "/api/event/all")
    private String path;
    @Schema(description = "에러 발생 시각", example = "2026-07-09T10:20:30+09:00")
    private String timestamp;

    public FailResult(String errorCode, String message) {
        this(errorCode, message, null, null, null);
    }

    public FailResult(String errorCode, String message, Integer status, String path, String timestamp) {
        this.errorCode = errorCode;
        this.message = message;
        this.status = status;
        this.path = path;
        this.timestamp = timestamp;
    }
}
