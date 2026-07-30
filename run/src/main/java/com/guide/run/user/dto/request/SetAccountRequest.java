package com.guide.run.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "아이디/비밀번호 최초 설정 요청")
public class SetAccountRequest {
    @NotBlank
    @Schema(description = "설정할 계정 ID", example = "runner01")
    private String accountId;

    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&*_]).{8,}$")
    @Schema(description = "비밀번호 (영문·숫자·특수문자(!@#$%^&*_) 포함 8자 이상)", example = "Pass1234!")
    private String password;
}
