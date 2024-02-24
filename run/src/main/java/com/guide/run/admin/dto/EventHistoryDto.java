package com.guide.run.admin.dto;

import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.event.entity.type.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;


@Getter
@AllArgsConstructor
@Builder
public class EventHistoryDto {
    private Long eventId;
    private EventType eventType;
    private String name;
    private LocalDate endDate;
    private EventRecruitStatus recruitStatus;
}
