package com.guide.run.event.entity.dto.response;

import com.guide.run.event.entity.type.EventRecruitStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class EventCreatedResponse {
    private Long eventId;
    private boolean isApprove;
}
