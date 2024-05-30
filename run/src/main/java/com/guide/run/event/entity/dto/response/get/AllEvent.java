package com.guide.run.event.entity.dto.response.get;

import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.event.entity.type.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Locale;

@Getter
public class AllEvent {
    private Long eventId;
    private EventType eventType;
    private String name;
    private String date;
    private EventRecruitStatus recruitStatus;

    public AllEvent(Long eventId, EventType eventType, String name, LocalDateTime date, EventRecruitStatus recruitStatus) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.name = name;
        this.date = date.getYear()+"."+date.getMonthValue()+"."+date.getDayOfMonth()+" "+date.getDayOfWeek().getDisplayName(TextStyle.NARROW, Locale.KOREA);
        this.recruitStatus = recruitStatus;
    }
}
