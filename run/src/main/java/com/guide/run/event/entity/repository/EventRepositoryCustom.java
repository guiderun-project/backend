package com.guide.run.event.entity.repository;

import com.guide.run.event.entity.Event;
import com.guide.run.event.entity.dto.response.calender.MyEventOfDayOfCalendar;
import com.guide.run.event.entity.dto.response.calender.MyEventOfMonth;
import com.guide.run.event.entity.dto.response.get.*;
import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.event.entity.type.EventType;
import com.guide.run.user.entity.type.UserType;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepositoryCustom {

    List<MyEvent> findMyEventByYear(String privateId, int year, EventRecruitStatus eventRecruitStatus);
    List<MyEventOfMonth> findMyEventsOfMonth(LocalDateTime startTime, LocalDateTime endTime,String privateId);
    List<MyEventOfDayOfCalendar> findMyEventsOfDay(LocalDateTime startTime,LocalDateTime endTime,String privateId);
    long getAllMyEventListCount(EventType eventType,EventRecruitStatus eventRecruitStatus,String privateId);
    List<AllEvent> getAllMyEventList(int limit,int start,EventType eventType,EventRecruitStatus eventRecruitStatus,String privateId);
    List<MyEventDday> getMyEventDday(String userId);

    List<Event> getSchedulerEvent();
    List<Event> getSchedulerRecruit();

    List<AllEvent> getAllEventList(int limit, int start, EventType eventType, EventRecruitStatus eventRecruitStatus);
}
