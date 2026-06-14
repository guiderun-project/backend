package com.guide.run.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "생년월일 등록 응답")
public class UserBirthDatePatchResponse {
    @Schema(description = "등록된 생년월일 (YYYY-MM-DD)", example = "1995-03-15")
    private String birthDate;
}
