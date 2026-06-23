package com.guide.run.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "러닝 정보 수정 요청")
public class UpdateRunningInfoRequest {
    @Schema(description = "러닝 등급", example = "A")
    private String recordDegree;

    @Schema(description = "세부 기록", example = "10km 55분", nullable = true)
    private String detailRecord;

    @Schema(description = "희망 훈련 지역/방식", example = "서울 한강", nullable = true)
    private String hopePrefs;
}
