package com.guide.run.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "생년월일 등록 요청")
public class UserBirthDatePatchRequest {
    @Schema(description = "생년월일 (YYYY-MM-DD)", example = "1995-03-15")
    private String birthDate;
}
