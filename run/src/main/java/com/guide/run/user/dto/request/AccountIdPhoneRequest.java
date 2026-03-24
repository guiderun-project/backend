package com.guide.run.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "비밀번호 재설정용 인증번호 요청")
public class AccountIdPhoneRequest {
    @Schema(description = "계정 ID", example = "runner01")
    private String accountId;
    @Schema(description = "본인 확인 휴대전화 번호", example = "01012345678")
    private String phoneNum;
}
