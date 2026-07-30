package com.guide.run.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "아이디/비밀번호 최초 설정 응답")
public class SetAccountResponse {
    @Schema(description = "설정된 계정 ID", example = "runner01")
    private String accountId;
}
