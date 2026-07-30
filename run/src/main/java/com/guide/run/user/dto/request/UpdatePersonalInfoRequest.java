package com.guide.run.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "내 정보 수정 요청")
public class UpdatePersonalInfoRequest {
    @Schema(description = "생년월일 (YYYY-MM-DD)", example = "1995-03-15")
    private String birthDate;

    @Schema(description = "전화번호", example = "01012345678")
    private String phoneNumber;

    @Schema(description = "SNS 아이디", example = "@runner", nullable = true)
    private String snsId;

    @Schema(description = "1365 아이디 (GUIDE 사용자만 사용)", example = "hong1365", nullable = true)
    private String id1365;
}
