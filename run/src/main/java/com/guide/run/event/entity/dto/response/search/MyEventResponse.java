package com.guide.run.event.entity.dto.response.search;

import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.event.entity.type.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
public class MyEventResponse {
    private Long eventId;
    private EventType eventType;
    private String name;
    private int dDay;
    private LocalDate endDate;
    private EventRecruitStatus recruitStatus;
}
