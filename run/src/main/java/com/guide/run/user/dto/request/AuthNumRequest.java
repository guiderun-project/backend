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
@Schema(description = "문자 인증번호 검증 요청")
public class AuthNumRequest {
    @Schema(description = "문자로 받은 인증번호", example = "123456")
    private String number;
}
