package com.guide.run.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "SMS 인증번호 발송 응답")
public class SmsVerificationIssueResponse {
    @Schema(description = "인증 세션 식별자", example = "550e8400-e29b-41d4-a716-446655440000")
    private String verificationId;

    @Schema(description = "인증 목적", example = "ACCOUNT_ID", allowableValues = {"ACCOUNT_ID", "PASSWORD"})
    private String purpose;

    @Schema(description = "인증번호 유효 시간(초)", example = "600")
    private int expiresInSeconds;

    @Schema(description = "인증번호 만료 일시(ISO 8601)", example = "2026-06-14T12:10:00+09:00")
    private String expiresAt;

    @Schema(description = "서버 현재 시각(ISO 8601)", example = "2026-06-14T12:00:00+09:00")
    private String serverTime;

    @Schema(description = "만료 연장 가능 여부", example = "false")
    private boolean canExtend;
}
