package com.guide.run.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "액세스 토큰 재발급 응답")
public class ReissuedAccessTokenDto {
    @Schema(description = "재발급된 액세스 토큰", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String accessToken;
    @Schema(description = "추가 회원가입 없이 바로 서비스 사용 가능한지 여부", example = "true")
    private Boolean isExist;
}
