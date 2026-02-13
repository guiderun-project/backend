package com.guide.run.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "로그인 응답")
public class LoginResponse {
    @Schema(description = "액세스 토큰", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String accessToken;
    @Schema(description = "리프레시 토큰", example = "dG9rZW4tcmVmcmVzaA...")
    private String refreshToken;
    @Schema(description = "회원 가입 여부", example = "true")
    private Boolean isExist;
}
