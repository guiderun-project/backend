package com.guide.run.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "일반 로그인 요청")
public class GeneralLoginRequest {
    @Schema(description = "로그인 계정 ID", example = "runner01")
    private String accountId;
    @Schema(description = "비밀번호", example = "p@ssword123")
    private String password;
}
