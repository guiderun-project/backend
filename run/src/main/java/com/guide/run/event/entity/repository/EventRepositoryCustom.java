package com.guide.run.event.entity.repository;

import com.guide.run.event.entity.Event;
import com.guide.run.event.entity.dto.response.calender.MyEventOfDayOfCalendar;
import com.guide.run.event.entity.dto.response.calender.MyEventOfMonth;
import com.guide.run.event.entity.dto.response.get.*;
import com.guide.run.event.entity.type.CityName;
import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.event.entity.type.EventType;
import java.time.LocalDateTime;
import java.util.List;

public interface EventRepositoryCustom {

    List<MyEvent> findMyEventByYear(String privateId, int year, EventRecruitStatus eventRecruitStatus);
    List<MyEventOfMonth> findMyEventsOfMonth(LocalDateTime startTime, LocalDateTime endTime,String privateId);
    List<MyEventOfDayOfCalendar> findMyEventsOfDay(LocalDateTime startTime,LocalDateTime endTime,String privateId);
    long getAllMyEventListCount(EventType eventType, EventRecruitStatus eventRecruitStatus, String privateId, CityName cityName);
    List<AllEvent> getAllMyEventList(int limit,int start,EventType eventType,EventRecruitStatus eventRecruitStatus,String privateId, CityName cityName);
    List<MyEventDday> getMyEventDday(String userId);

    List<Event> getSchedulerEvent();
    List<Event> getSchedulerRecruit();

    List<AllEvent> getAllEventList(int limit, int start, EventType eventType, EventRecruitStatus eventRecruitStatus, CityName cityName);
    List<AllEvent> upcomingGetAllEventList(int limit, int start, EventType eventType, EventRecruitStatus eventRecruitStatus, CityName cityName);

    long countByPrivateIdAndCityName(String privateId, CityName cityName);

    long updateRecruitEndForClosedEvents();

    List<AllEvent> getSearchEventList(int limit, int start, String title, EventType eventType, EventRecruitStatus eventRecruitStatus, CityName cityName);
    List<AllEvent> upcomingGetSearchEventList(int limit, int start, String title, EventType eventType, EventRecruitStatus eventRecruitStatus, CityName cityName);
    List<AllEvent> getMySearchEventList(int limit, int start, String title, EventType eventType, EventRecruitStatus eventRecruitStatus, String privateId, CityName cityName);
    long getSearchEventListCount(String title, EventType eventType, EventRecruitStatus eventRecruitStatus, CityName cityName);
    long getMySearchEventListCount(String title, EventType eventType, EventRecruitStatus eventRecruitStatus, String privateId, CityName cityName);

    long countApprovedEventsByYear(int year);
    double sumDistanceByYear(int year);
    long countMyParticipation(String privateId);
    double sumMyParticipationDistance(String privateId);
}
