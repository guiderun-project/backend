package com.guide.run.admin.dto.response.event;

import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.event.entity.type.EventType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CurrentEventResponse {
    private long eventId;
    private EventType eventType;
    private String title;
    private EventRecruitStatus recruitStatus;
}
