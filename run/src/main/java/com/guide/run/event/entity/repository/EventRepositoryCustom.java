package com.guide.run.event.entity.repository;

import com.guide.run.event.entity.dto.response.get.*;
import com.guide.run.event.entity.type.CityName;
import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.event.entity.type.EventType;
import com.guide.run.user.dto.response.MyActivityEventsResponse;
import java.util.List;

public interface EventRepositoryCustom {

    long getAllMyEventListCount(EventType eventType, EventRecruitStatus eventRecruitStatus, String privateId, CityName cityName);
    List<AllEvent> getAllMyEventList(int limit,int start,EventType eventType,EventRecruitStatus eventRecruitStatus,String privateId, CityName cityName);
    long countEventList(EventType eventType, EventRecruitStatus eventRecruitStatus, CityName cityName);
    long countUpcomingEventList(EventType eventType, EventRecruitStatus eventRecruitStatus, CityName cityName, boolean includePrivate);
    long countPastEventList(EventType eventType, CityName cityName, boolean includePrivate);
    List<AllEvent> getAllEventList(int limit, int start, EventType eventType, EventRecruitStatus eventRecruitStatus, CityName cityName);
    List<AllEvent> upcomingGetAllEventList(int limit, int start, EventType eventType, EventRecruitStatus eventRecruitStatus, CityName cityName, boolean includePrivate);
    List<AllEvent> pastGetAllEventList(int limit, int start, EventType eventType, CityName cityName, boolean includePrivate);

    long countByPrivateIdAndCityName(String privateId, CityName cityName);

    long updateRecruitEndForClosedEvents();

    List<AllEvent> getSearchEventList(int limit, int start, String title, EventType eventType, EventRecruitStatus eventRecruitStatus, CityName cityName);
    List<AllEvent> upcomingGetSearchEventList(int limit, int start, String title, EventType eventType, EventRecruitStatus eventRecruitStatus, CityName cityName);
    List<AllEvent> pastGetSearchEventList(int limit, int start, String title, EventType eventType, CityName cityName);
    List<AllEvent> getMySearchEventList(int limit, int start, String title, EventType eventType, EventRecruitStatus eventRecruitStatus, String privateId, CityName cityName);
    long upcomingGetSearchEventListCount(String title, EventType eventType, EventRecruitStatus eventRecruitStatus, CityName cityName);
    long pastGetSearchEventListCount(String title, EventType eventType, CityName cityName);
    long getSearchEventListCount(String title, EventType eventType, EventRecruitStatus eventRecruitStatus, CityName cityName);
    long getMySearchEventListCount(String title, EventType eventType, EventRecruitStatus eventRecruitStatus, String privateId, CityName cityName);

    long countApprovedEventsByYear(int year);
    double sumDistanceByYear(int year);
    long countMyParticipation(String privateId);
    double sumMyParticipationDistance(String privateId);

    List<MyActivityEventsResponse.Item> findActivityEvents(String privateId, EventType type, String relation, int page, int size);
    long countActivityEvents(String privateId, EventType type, String relation);
}
