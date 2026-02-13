package com.guide.run.event.entity.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "이벤트 생성 응답")
public class EventCreatedResponse {
    @Schema(description = "생성된 이벤트 ID", example = "1023")
    private Long eventId;
    @Schema(description = "승인 필요 여부", example = "false")
    private boolean isApprove;
}
