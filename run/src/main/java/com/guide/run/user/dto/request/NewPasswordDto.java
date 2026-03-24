package com.guide.run.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "비밀번호 재설정 요청")
public class NewPasswordDto {
    @Schema(description = "인증 완료 후 발급된 임시 토큰", example = "tmp-token-1234")
    private String token;
    @Schema(description = "새 비밀번호", example = "n3wP@ssword!")
    private String newPassword;
}
