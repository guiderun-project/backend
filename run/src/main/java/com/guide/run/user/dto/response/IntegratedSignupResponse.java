package com.guide.run.user.dto.response;

import com.guide.run.user.entity.type.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "통합 회원가입 완료 응답")
public class IntegratedSignupResponse {
    @Schema(description = "생성된 공개 사용자 ID", example = "guide_102")
    private String userId;
    @Schema(description = "액세스 토큰", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String accessToken;
    @Schema(description = "리프레시 토큰", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String refreshToken;
    @Schema(description = "회원 권한", example = "ROLE_WAIT")
    private String role;
    @Schema(description = "사용자 유형", example = "VI")
    private UserType disabilityType;
}
