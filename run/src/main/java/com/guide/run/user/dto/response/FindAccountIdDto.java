package com.guide.run.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "아이디 찾기 응답")
public class FindAccountIdDto {
    @Schema(description = "찾은 계정 ID", example = "runner01")
    private String accountId;
    @Schema(description = "계정 생성일", example = "2025-03-01")
    private String createdAt;
}
