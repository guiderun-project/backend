package com.guide.run.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "약관 동의 정보")
public class PermissionDto {
    @Schema(description = "개인정보 수집 및 이용 동의", example = "true")
    private boolean privacy;
    @Schema(description = "초상권 활용 동의", example = "true")
    private boolean portraitRights;
}
