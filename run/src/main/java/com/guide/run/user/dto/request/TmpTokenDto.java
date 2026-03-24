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
@Schema(description = "임시 토큰 기반 조회 요청")
public class TmpTokenDto {
    @Schema(description = "아이디 찾기/비밀번호 재설정에 사용할 임시 토큰", example = "tmp-token-1234")
    private String token;
}
