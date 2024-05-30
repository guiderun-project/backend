package com.guide.run.event.entity.dto.response.calender;

import com.guide.run.event.entity.type.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class MyEventOfMonth {
    EventType eventType;
    LocalDateTime startTime;
}
