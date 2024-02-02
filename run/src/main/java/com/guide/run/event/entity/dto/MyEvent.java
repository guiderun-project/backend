package com.guide.run.event.entity.dto;

import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.event.entity.type.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.Period;

@Getter
@Builder
@AllArgsConstructor
public class MyEvent {
    private Long eventId;
    private EventType eventType;
    private String name;
    private int dDay;
    private LocalDate endDate;
    private EventRecruitStatus recruitStatus;
}
