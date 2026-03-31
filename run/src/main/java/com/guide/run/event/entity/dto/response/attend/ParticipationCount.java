package com.guide.run.event.entity.dto.response.attend;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "이벤트 신청 현황 개수 응답")
public class ParticipationCount {
    @Schema(description = "총 신청자 수", example = "10")
    private Long count;
    @Schema(description = "VI 신청자 수", example = "4")
    private Long vi;
    @Schema(description = "Guide 신청자 수", example = "6")
    private Long guide;
}
