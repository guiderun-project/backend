package com.guide.run.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Schema(description = "문자 인증번호 검증 요청")
public class AuthNumRequest {
    @Schema(description = "인증 세션 식별자 (발송 API 응답의 verificationId)", example = "550e8400-e29b-41d4-a716-446655440000")
    private String verificationId;

    @Schema(description = "문자로 받은 인증번호", example = "12345")
    private String number;
}
