package com.guide.run.event.entity.repository;

import com.guide.run.admin.dto.EventDto;
import com.guide.run.event.entity.dto.response.calender.MyEventOfDayOfCalendar;
import com.guide.run.event.entity.dto.response.calender.MyEventOfMonth;
import com.guide.run.event.entity.dto.response.get.AllEvent;
import com.guide.run.event.entity.dto.response.get.MyEvent;
import com.guide.run.event.entity.dto.response.get.MyEventDday;
import com.guide.run.event.entity.dto.response.get.MyPageEvent;
import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.event.entity.type.EventType;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface EventRepositoryCustom {
    List<MyPageEvent> findMyEventAfterYear(String privateId, int start, int limit, String kind, int year);

    long countMyEventAfterYear(String privateId, String kind, int year);

    List<EventDto> sortAdminEvent(int start, int limit);
    long sortAdminEventCount();
    List<MyEvent> findMyEventByYear(String privateId, int year, EventRecruitStatus eventRecruitStatus);
    List<MyEventOfMonth> findMyEventsOfMonth(LocalDateTime startTime, LocalDateTime endTime,String privateId);
    List<MyEventOfDayOfCalendar> findMyEventsOfDay(LocalDateTime startTime,LocalDateTime endTime,String privateId);
    long getAllMyEventListCount(EventType eventType,EventRecruitStatus eventRecruitStatus,String privateId);
    List<AllEvent> getAllMyEventList(int limit,int start,EventType eventType,EventRecruitStatus eventRecruitStatus,String privateId);
    List<MyEventDday> getMyEventDday(String userId);
}
