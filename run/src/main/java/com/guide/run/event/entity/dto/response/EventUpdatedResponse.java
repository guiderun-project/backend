package com.guide.run.event.entity.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class EventUpdatedResponse {
    private Long eventId;
    private boolean isApprove;
}
