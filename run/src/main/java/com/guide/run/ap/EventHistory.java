package com.guide.run.ap;

import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.event.entity.type.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;


@Getter
@AllArgsConstructor
@Builder
public class EventHistory {
    private Long eventId;
    private EventType eventType;
    private String name;
    private LocalDate endDate;
    private EventRecruitStatus recruitStatus;
}
