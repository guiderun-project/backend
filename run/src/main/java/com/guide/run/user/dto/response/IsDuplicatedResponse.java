package com.guide.run.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "계정 ID 중복 확인 응답")
public class IsDuplicatedResponse {
    @Schema(description = "사용 가능한 계정 ID 여부", example = "true")
    private Boolean isUnique;
}
