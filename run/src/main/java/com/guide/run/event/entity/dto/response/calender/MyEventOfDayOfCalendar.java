package com.guide.run.event.entity.dto.response.calender;

import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.event.entity.type.EventType;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MyEventOfDayOfCalendar {
    long eventId;
    EventType eventType;
    String name;
    String startDate;
    EventRecruitStatus recruitStatus;

    public MyEventOfDayOfCalendar(long eventId, EventType eventType, String name, LocalDateTime startDate, EventRecruitStatus recruitStatus) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.name = name;
        this.startDate = startDate.getYear()+"."+ startDate.getMonthValue()+"."+ startDate.getDayOfMonth();
        this.recruitStatus = recruitStatus;
    }
}
