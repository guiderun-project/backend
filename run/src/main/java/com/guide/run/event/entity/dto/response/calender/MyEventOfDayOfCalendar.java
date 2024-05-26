package com.guide.run.event.entity.dto.response.calender;

import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.event.entity.type.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MyEventOfDayOfCalendar {
    long eventId;
    EventType eventType;
    String name;
    String endDate; //startDate로 바꿔야 하지않나
    EventRecruitStatus recruitStatus;

    public MyEventOfDayOfCalendar(long eventId, EventType eventType, String name, LocalDateTime endDate, EventRecruitStatus recruitStatus) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.name = name;
        this.endDate = endDate.getYear()+"."+endDate.getMonthValue()+"."+endDate.getDayOfMonth();
        this.recruitStatus = recruitStatus;
    }
}
