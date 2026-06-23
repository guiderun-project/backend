package com.guide.run.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "인증번호 제한시간 연장 요청")
public class SmsVerificationExtendRequest {
    @Schema(description = "인증 세션 식별자", example = "550e8400-e29b-41d4-a716-446655440000")
    private String verificationId;
}
