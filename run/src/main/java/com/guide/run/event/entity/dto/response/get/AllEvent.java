package com.guide.run.event.entity.dto.response.get;

import com.guide.run.event.service.EventTemporalStatusResolver;
import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.event.entity.type.EventType;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Locale;

@Getter
public class AllEvent {
    private Long id;
    private EventRecruitStatus recruitStatus;
    private String name;
    private EventType type;
    private String dateText;

    public AllEvent(Long eventId, EventType eventType, String name, LocalDateTime startDate, EventRecruitStatus recruitStatus) {
        this.id = eventId;
        this.recruitStatus = toResponseRecruitStatus(recruitStatus);
        this.name = name;
        this.type = eventType;
        this.dateText = String.format(
                "%04d. %02d. %02d %s",
                startDate.getYear(),
                startDate.getMonthValue(),
                startDate.getDayOfMonth(),
                startDate.getDayOfWeek().getDisplayName(TextStyle.NARROW, Locale.KOREA)
        );
    }

    public AllEvent(Long eventId,
                    EventType eventType,
                    String name,
                    LocalDateTime startDate,
                    LocalDateTime endDate,
                    LocalDate recruitStartDate,
                    LocalDate recruitEndDate,
                    EventRecruitStatus recruitStatus) {
        this.id = eventId;
        this.recruitStatus = EventTemporalStatusResolver.resolveRecruitStatus(
                recruitStatus,
                recruitStartDate,
                recruitEndDate,
                startDate
        );
        this.name = name;
        this.type = eventType;
        this.dateText = String.format(
                "%04d. %02d. %02d %s",
                startDate.getYear(),
                startDate.getMonthValue(),
                startDate.getDayOfMonth(),
                startDate.getDayOfWeek().getDisplayName(TextStyle.NARROW, Locale.KOREA)
        );
    }

    private EventRecruitStatus toResponseRecruitStatus(EventRecruitStatus recruitStatus) {
        if (recruitStatus == EventRecruitStatus.RECRUIT_END) {
            return EventRecruitStatus.RECRUIT_CLOSE;
        }
        return recruitStatus;
    }
}
