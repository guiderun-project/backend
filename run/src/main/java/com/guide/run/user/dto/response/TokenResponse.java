package com.guide.run.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "임시 인증 토큰 응답")
public class TokenResponse {
    @Schema(description = "아이디 찾기/비밀번호 재설정에 사용하는 임시 토큰", example = "tmp-token-1234")
    private String token;
}
