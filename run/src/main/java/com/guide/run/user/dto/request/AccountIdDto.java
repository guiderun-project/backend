package com.guide.run.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "회원가입 계정 ID 중복 확인 요청")
public class AccountIdDto {
    @Schema(description = "확인할 계정 ID", example = "runner01")
    private String accountId;
}
