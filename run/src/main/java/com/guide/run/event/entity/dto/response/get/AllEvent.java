package com.guide.run.event.entity.dto.response.get;

import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.event.entity.type.EventType;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Locale;

@Getter
public class AllEvent {
    private Long eventId;
    private EventType eventType;
    private String name;
    private String startDate;
    private EventRecruitStatus recruitStatus;

    public AllEvent(Long eventId, EventType eventType, String name, LocalDateTime startDate, EventRecruitStatus recruitStatus) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.name = name;
        this.startDate = startDate.getYear()+"."+ startDate.getMonthValue()+"."+ startDate.getDayOfMonth()+" "+ startDate.getDayOfWeek().getDisplayName(TextStyle.NARROW, Locale.KOREA);
        this.recruitStatus = recruitStatus;
    }
}
