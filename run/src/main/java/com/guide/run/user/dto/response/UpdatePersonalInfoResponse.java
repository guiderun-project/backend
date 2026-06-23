package com.guide.run.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "내 정보 수정 응답")
public class UpdatePersonalInfoResponse {
    @Schema(description = "생년월일 (YYYY-MM-DD)", example = "1995-03-15")
    private String birthDate;

    @Schema(description = "전화번호", example = "01012345678")
    private String phoneNumber;

    @Schema(description = "SNS 아이디", example = "@runner", nullable = true)
    private String snsId;

    @Schema(description = "1365 아이디", example = "hong1365", nullable = true)
    private String id1365;
}
