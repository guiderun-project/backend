package com.guide.run.event.entity.repository;

import com.guide.run.event.entity.Event;
import com.guide.run.event.entity.dto.response.calender.MyEventOfDayOfCalendar;
import com.guide.run.event.entity.dto.response.calender.MyEventOfMonth;
import com.guide.run.event.entity.dto.response.get.AllEvent;
import com.guide.run.event.entity.dto.response.get.MyEvent;
import com.guide.run.event.entity.dto.response.get.MyEventDday;
import com.guide.run.event.service.EventTemporalStatusResolver;
import com.guide.run.user.dto.response.MyActivityEventsResponse;
import com.querydsl.jpa.JPAExpressions;
import com.guide.run.event.entity.type.CityName;
import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.event.entity.type.EventStatus;
import com.guide.run.event.entity.type.EventType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.guide.run.event.entity.QEvent.event;
import static com.guide.run.event.entity.QEventForm.eventForm;
import static com.guide.run.event.entity.type.EventFormStatus.APPLIED;
import static com.guide.run.event.entity.type.EventRecruitStatus.*;
import static java.time.temporal.ChronoUnit.DAYS;

public class EventRepositoryImpl implements EventRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public EventRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }


    @Override
    public List<MyEvent> findMyEventByYear(String privateId, int year, EventRecruitStatus eventRecruitStatus){
        if(eventRecruitStatus.equals(RECRUIT_END)){
            return queryFactory.select(
                    Projections.constructor(MyEvent.class,
                            event.id.as("eventId"),
                            event.type.as("eventType"),
                            event.name.as("name"),
                            event.recruitStatus.as("recruitStatus"),
                            event.recruitStartDate.as("recruitStartDate"),
                            event.recruitEndDate.as("recruitEndDate"),
                            event.startTime.as("startDate"),
                            event.endTime.as("endDate"))
                    )
                    .from(event)
                    .join(eventForm).on(event.id.eq(eventForm.eventId),
                            eventForm.privateId.eq(privateId))
                    .where(checkByPastDateTime()
                            .and(event.isApprove.eq(true))
                            .and(event.startTime.year().eq(year))
                            .and(event.id.eq(eventForm.eventId))
                            .and(eventForm.privateId.eq(privateId)))
                    .orderBy(event.endTime.desc())
                    .offset(0)
                    .limit(4)
                    .fetch();
        }else{
            List<MyEvent> fetch = queryFactory.select(
                            Projections.constructor(MyEvent.class,
                                    event.id.as("eventId"),
                                    event.type.as("eventType"),
                                    event.name.as("name"),
                                    event.recruitStatus.as("recruitStatus"),
                                    event.recruitStartDate.as("recruitStartDate"),
                                    event.recruitEndDate.as("recruitEndDate"),
                                    event.startTime.as("startDate"),
                                    event.endTime.as("endDate"))
                    )
                    .from(event)
                    .join(eventForm).on(event.id.eq(eventForm.eventId),
                            eventForm.privateId.eq(privateId))
                    .where(checkByNotEndedDateTime()
                            .and(event.isApprove.eq(true))
                            .and(event.startTime.year().eq(year))
                            .and(event.id.eq(eventForm.eventId))
                            .and(eventForm.privateId.eq(privateId)))
                    .orderBy(event.endTime.asc())
                    .offset(0)
                    .limit(4)
                    .fetch();
            for(MyEvent myEvent : fetch){
                myEvent.setdDay((int)DAYS.between(EventTemporalStatusResolver.today(),myEvent.getEndDate()));
            }
            return fetch;
        }
    }

    @Override
    public List<MyEventOfMonth> findMyEventsOfMonth(LocalDateTime startTime, LocalDateTime endTime, String privateId){
        List<MyEventOfMonth> fetch = queryFactory.select(
                        Projections.constructor(MyEventOfMonth.class,
                                event.type.as("eventType"),
                                event.startTime.as("startTime"))
                )
                .from(event)
                .join(eventForm).on(event.id.eq(eventForm.eventId),
                        eventForm.privateId.eq(privateId))
                .where(event.startTime.between(startTime, endTime).and(event.isApprove.eq(true)))
                .orderBy(event.startTime.desc())
                .fetch();

        return fetch;
    }

    @Override
    public List<MyEventOfDayOfCalendar> findMyEventsOfDay(LocalDateTime startTime,LocalDateTime endTime, String privateId) {
        List<MyEventOfDayOfCalendar> fetch = queryFactory.select(
                        Projections.constructor(MyEventOfDayOfCalendar.class,
                                event.id.as("eventId"),
                                event.type.as("eventType"),
                                event.name.as("name"),
                                event.startTime.as("startDate"),
                                event.endTime.as("endDate"),
                                event.recruitStartDate.as("recruitStartDate"),
                                event.recruitEndDate.as("recruitEndDate"),
                                event.recruitStatus.as("recruitStatus"))
                )
                .from(event)
                .join(eventForm).on(event.id.eq(eventForm.eventId),
                        eventForm.privateId.eq(privateId))
                .where(
                        event.id.eq(eventForm.eventId),
                        eventForm.privateId.eq(privateId),
                        event.startTime.between(startTime, endTime).and(event.isApprove.eq(true)))
                .orderBy(event.startTime.desc())
                .fetch();

        return fetch;
    }

    @Override
    public long getAllMyEventListCount(EventType eventType, EventRecruitStatus eventRecruitStatus, String privateId, CityName cityName) {
        return queryFactory.select(event.count())
                .from(event)
                .join(eventForm).on(eventForm.eventId.eq(event.id))
                .where(checkByKind(eventRecruitStatus).and(checkByType(eventType)).and(event.isApprove.eq(true))
                        .and(eventForm.privateId.eq(privateId))
                        .and(eventForm.eventId.eq(event.id))
                        .and(checkByCityName(cityName))
                )
                .fetchOne();
    }

    @Override
    public List<AllEvent> getAllMyEventList(int limit, int start, EventType eventType, EventRecruitStatus eventRecruitStatus, String privateId, CityName cityName) {
        return queryFactory.select(Projections.constructor(AllEvent.class,
                event.id.as("eventId"),
                event.type.as("eventType"),
                event.name.as("name"),
                event.startTime.as("date"),
                event.endTime.as("endDate"),
                event.recruitStartDate.as("recruitStartDate"),
                event.recruitEndDate.as("recruitEndDate"),
                event.recruitStatus.as("recruitStatus")))
                .from(event)
                .join(eventForm).on(eventForm.eventId.eq(event.id))
                .where(checkByKind(eventRecruitStatus).and(checkByType(eventType)).and(event.isApprove.eq(true))
                        .and(eventForm.privateId.eq(privateId))
                        .and(eventForm.eventId.eq(event.id))
                        .and(checkByCityName(cityName))
                )
                .orderBy(event.startTime.desc())
                .offset(start)
                .limit(limit)
                .fetch();
    }

    @Override
    public List<MyEventDday> getMyEventDday(String privateId) {
        return queryFactory.select(Projections.constructor(MyEventDday.class,
                event.name.as("name"),
                event.startTime.as("dDay")))
                .from(event)
                .join(eventForm).on(event.id.eq(eventForm.eventId),
                        eventForm.privateId.eq(privateId))
                .where(checkByNotEndedDateTime()
                        .and(event.isApprove.eq(true))
                        .and(eventForm.eventId.eq(event.id))
                        .and(eventForm.privateId.eq(privateId)))
                .orderBy(event.startTime.asc())
                .limit(2)
                .fetch();
    }

    @Override
    public List<Event> getSchedulerEvent() {
        return queryFactory.selectFrom(event)
                .where(event.recruitStatus.eq(RECRUIT_CLOSE),
                        event.status.ne(EventStatus.EVENT_END))
                .fetch();
    }

    @Override
    public List<Event> getSchedulerRecruit() {
        return queryFactory.selectFrom(event)
                .where(event.status.eq(EventStatus.EVENT_UPCOMING),
                        event.recruitStatus.ne(RECRUIT_END),
                        event.recruitStatus.ne(RECRUIT_CLOSE))
                .fetch();
    }

    @Override
    public long countEventList(EventType eventType, EventRecruitStatus eventRecruitStatus, CityName cityName) {
        Long result = queryFactory.select(event.count())
                .from(event)
                .where(checkByKind(eventRecruitStatus)
                        .and(checkByType(eventType))
                        .and(event.isApprove.eq(true))
                        .and(checkByCityName(cityName))
                        .and(checkByPublicEvent())
                )
                .fetchOne();
        return result != null ? result : 0L;
    }

    @Override
    public long countUpcomingEventList(EventType eventType, EventRecruitStatus eventRecruitStatus, CityName cityName) {
        Long result = queryFactory.select(event.count())
                .from(event)
                .where(checkByKind(eventRecruitStatus)
                        .and(checkByType(eventType))
                        .and(event.isApprove.eq(true))
                        .and(checkByCityName(cityName))
                        .and(checkByPublicEvent())
                        .and(checkByNotEndedDateTime())
                )
                .fetchOne();
        return result != null ? result : 0L;
    }

    @Override
    public long countPastEventList(EventType eventType, CityName cityName) {
        Long result = queryFactory.select(event.count())
                .from(event)
                .where(checkByType(eventType)
                        .and(event.isApprove.eq(true))
                        .and(checkByCityName(cityName))
                        .and(checkByPublicEvent())
                        .and(checkByPastDateTime())
                )
                .fetchOne();
        return result != null ? result : 0L;
    }

    @Override
    public List<AllEvent> getAllEventList(int limit, int start, EventType eventType, EventRecruitStatus eventRecruitStatus,CityName cityName) {
        return queryFactory.select(Projections.constructor(AllEvent.class,
                        event.id.as("eventId"),
                        event.type.as("eventType"),
                        event.name.as("name"),
                        event.startTime.as("date"),
                        event.endTime.as("endDate"),
                        event.recruitStartDate.as("recruitStartDate"),
                        event.recruitEndDate.as("recruitEndDate"),
                        event.recruitStatus.as("recruitStatus")))
                .from(event)
                .where(checkByKind(eventRecruitStatus)
                        .and(checkByType(eventType))
                        .and(event.isApprove.eq(true))
                        .and(checkByCityName(cityName))
                        .and(checkByPublicEvent())
                )
                .orderBy(event.startTime.desc())
                .offset(start)
                .limit(limit)
                .fetch();
    }
    @Override
    public List<AllEvent> upcomingGetAllEventList(int limit, int start, EventType eventType, EventRecruitStatus eventRecruitStatus,CityName cityName) {
        return queryFactory.select(Projections.constructor(AllEvent.class,
                        event.id.as("eventId"),
                        event.type.as("eventType"),
                        event.name.as("name"),
                        event.startTime.as("date"),
                        event.endTime.as("endDate"),
                        event.recruitStartDate.as("recruitStartDate"),
                        event.recruitEndDate.as("recruitEndDate"),
                        event.recruitStatus.as("recruitStatus")))
                .from(event)
                .where(checkByKind(eventRecruitStatus)
                        .and(checkByType(eventType))
                        .and(event.isApprove.eq(true))
                        .and(checkByCityName(cityName))
                        .and(checkByPublicEvent())
                        .and(checkByNotEndedDateTime())
                )
                .orderBy(event.startTime.asc())
                .offset(start)
                .limit(limit)
                .fetch();
    }

    @Override
    public List<AllEvent> pastGetAllEventList(int limit, int start, EventType eventType, CityName cityName) {
        return queryFactory.select(Projections.constructor(AllEvent.class,
                        event.id.as("eventId"),
                        event.type.as("eventType"),
                        event.name.as("name"),
                        event.startTime.as("date"),
                        event.endTime.as("endDate"),
                        event.recruitStartDate.as("recruitStartDate"),
                        event.recruitEndDate.as("recruitEndDate"),
                        event.recruitStatus.as("recruitStatus")))
                .from(event)
                .where(checkByType(eventType)
                        .and(event.isApprove.eq(true))
                        .and(checkByCityName(cityName))
                        .and(checkByPublicEvent())
                        .and(checkByPastDateTime())
                )
                .orderBy(event.startTime.desc())
                .offset(start)
                .limit(limit)
                .fetch();
    }

    @Override
    public long countByPrivateIdAndCityName(String privateId, CityName cityName) {
        return queryFactory
                .select(eventForm.count())
                .from(eventForm)
                .join(event).on(eventForm.eventId.eq(event.id))
                .where(checkByCityName(cityName)
                        .and(eventForm.privateId.eq(privateId)))
                .fetchOne();
    }

    @Override
    public long updateRecruitEndForClosedEvents() {
        return queryFactory
                .update(event)
                .set(event.recruitStatus, EventRecruitStatus.RECRUIT_END)
                .where(event.recruitStatus.eq(EventRecruitStatus.RECRUIT_CLOSE)
                        .and(event.status.eq(EventStatus.EVENT_END)))
                .execute();
    }

    @Override
    public List<AllEvent> getSearchEventList(int limit, int start, String title, EventType eventType, EventRecruitStatus eventRecruitStatus, CityName cityName) {
        return queryFactory.select(Projections.constructor(AllEvent.class,
                        event.id.as("eventId"),
                        event.type.as("eventType"),
                        event.name.as("name"),
                        event.startTime.as("date"),
                        event.endTime.as("endDate"),
                        event.recruitStartDate.as("recruitStartDate"),
                        event.recruitEndDate.as("recruitEndDate"),
                        event.recruitStatus.as("recruitStatus")))
                .from(event)
                .where(checkByKind(eventRecruitStatus)
                        .and(checkByType(eventType))
                        .and(event.isApprove.eq(true))
                        .and(checkByCityName(cityName))
                        .and(checkByPublicEvent())
                        .and(checkByTitle(title))
                )
                .orderBy(event.startTime.desc())
                .offset(start)
                .limit(limit)
                .fetch();
    }

    @Override
    public List<AllEvent> upcomingGetSearchEventList(int limit, int start, String title, EventType eventType, EventRecruitStatus eventRecruitStatus, CityName cityName) {
        return queryFactory.select(Projections.constructor(AllEvent.class,
                        event.id.as("eventId"),
                        event.type.as("eventType"),
                        event.name.as("name"),
                        event.startTime.as("date"),
                        event.endTime.as("endDate"),
                        event.recruitStartDate.as("recruitStartDate"),
                        event.recruitEndDate.as("recruitEndDate"),
                        event.recruitStatus.as("recruitStatus")))
                .from(event)
                .where(checkByKind(eventRecruitStatus)
                        .and(checkByType(eventType))
                        .and(event.isApprove.eq(true))
                        .and(checkByCityName(cityName))
                        .and(checkByPublicEvent())
                        .and(checkByTitle(title))
                        .and(checkByNotEndedDateTime())
                )
                .orderBy(event.startTime.asc())
                .offset(start)
                .limit(limit)
                .fetch();
    }

    @Override
    public List<AllEvent> pastGetSearchEventList(int limit, int start, String title, EventType eventType, CityName cityName) {
        return queryFactory.select(Projections.constructor(AllEvent.class,
                        event.id.as("eventId"),
                        event.type.as("eventType"),
                        event.name.as("name"),
                        event.startTime.as("date"),
                        event.endTime.as("endDate"),
                        event.recruitStartDate.as("recruitStartDate"),
                        event.recruitEndDate.as("recruitEndDate"),
                        event.recruitStatus.as("recruitStatus")))
                .from(event)
                .where(checkByType(eventType)
                        .and(event.isApprove.eq(true))
                        .and(checkByCityName(cityName))
                        .and(checkByPublicEvent())
                        .and(checkByTitle(title))
                        .and(checkByPastDateTime())
                )
                .orderBy(event.startTime.desc())
                .offset(start)
                .limit(limit)
                .fetch();
    }

    @Override
    public List<AllEvent> getMySearchEventList(int limit, int start, String title, EventType eventType, EventRecruitStatus eventRecruitStatus, String privateId, CityName cityName) {
        return queryFactory.select(Projections.constructor(AllEvent.class,
                        event.id.as("eventId"),
                        event.type.as("eventType"),
                        event.name.as("name"),
                        event.startTime.as("date"),
                        event.endTime.as("endDate"),
                        event.recruitStartDate.as("recruitStartDate"),
                        event.recruitEndDate.as("recruitEndDate"),
                        event.recruitStatus.as("recruitStatus")))
                .from(event)
                .join(eventForm).on(eventForm.eventId.eq(event.id))
                .where(checkByKind(eventRecruitStatus)
                        .and(checkByType(eventType))
                        .and(event.isApprove.eq(true))
                        .and(eventForm.privateId.eq(privateId))
                        .and(checkByCityName(cityName))
                        .and(checkByTitle(title))
                )
                .orderBy(event.startTime.desc())
                .offset(start)
                .limit(limit)
                .fetch();
    }

    @Override
    public long upcomingGetSearchEventListCount(String title, EventType eventType, EventRecruitStatus eventRecruitStatus, CityName cityName) {
        Long result = queryFactory.select(event.count())
                .from(event)
                .where(checkByKind(eventRecruitStatus)
                        .and(checkByType(eventType))
                        .and(event.isApprove.eq(true))
                        .and(checkByCityName(cityName))
                        .and(checkByPublicEvent())
                        .and(checkByTitle(title))
                        .and(checkByNotEndedDateTime())
                )
                .fetchOne();
        return result != null ? result : 0L;
    }

    @Override
    public long pastGetSearchEventListCount(String title, EventType eventType, CityName cityName) {
        Long result = queryFactory.select(event.count())
                .from(event)
                .where(checkByType(eventType)
                        .and(event.isApprove.eq(true))
                        .and(checkByCityName(cityName))
                        .and(checkByPublicEvent())
                        .and(checkByTitle(title))
                        .and(checkByPastDateTime())
                )
                .fetchOne();
        return result != null ? result : 0L;
    }

    @Override
    public long getSearchEventListCount(String title, EventType eventType, EventRecruitStatus eventRecruitStatus, CityName cityName) {
        return queryFactory.select(event.count())
                .from(event)
                .where(checkByKind(eventRecruitStatus)
                        .and(checkByType(eventType))
                        .and(event.isApprove.eq(true))
                        .and(checkByCityName(cityName))
                        .and(checkByPublicEvent())
                        .and(checkByTitle(title))
                )
                .fetchOne();
    }

    @Override
    public long getMySearchEventListCount(String title, EventType eventType, EventRecruitStatus eventRecruitStatus, String privateId, CityName cityName) {
        return queryFactory.select(event.count())
                .from(event)
                .join(eventForm).on(eventForm.eventId.eq(event.id))
                .where(checkByKind(eventRecruitStatus)
                        .and(checkByType(eventType))
                        .and(event.isApprove.eq(true))
                        .and(eventForm.privateId.eq(privateId))
                        .and(checkByCityName(cityName))
                        .and(checkByTitle(title))
                )
                .fetchOne();
    }

    @Override
    public List<MyActivityEventsResponse.Item> findActivityEvents(String privateId, EventType type, String relation, int page, int size) {
        return queryFactory.select(
                        Projections.constructor(MyActivityEventsResponse.Item.class,
                                event.id,
                                event.name,
                                event.type,
                                event.startTime))
                .from(event)
                .where(event.isApprove.eq(true)
                        .and(activityTypeCond(type))
                        .and(activityRelationCond(privateId, relation)))
                .orderBy(event.startTime.desc())
                .offset((long) page * size)
                .limit(size)
                .fetch();
    }

    @Override
    public long countActivityEvents(String privateId, EventType type, String relation) {
        Long result = queryFactory.select(event.count())
                .from(event)
                .where(event.isApprove.eq(true)
                        .and(activityTypeCond(type))
                        .and(activityRelationCond(privateId, relation)))
                .fetchOne();
        return result != null ? result : 0L;
    }

    private BooleanBuilder activityTypeCond(EventType type) {
        if (type == null || type == EventType.TOTAL) return new BooleanBuilder();
        return new BooleanBuilder(event.type.eq(type));
    }

    private BooleanBuilder activityRelationCond(String privateId, String relation) {
        if ("PARTICIPATED".equals(relation)) {
            return new BooleanBuilder(activeParticipationExists(privateId));
        } else if ("HOSTED".equals(relation)) {
            return new BooleanBuilder(event.organizer.eq(privateId));
        } else {
            return new BooleanBuilder(
                    event.organizer.eq(privateId)
                            .or(activeParticipationExists(privateId)));
        }
    }

    private BooleanExpression activeParticipationExists(String privateId) {
        return JPAExpressions.selectFrom(eventForm)
                .where(eventForm.eventId.eq(event.id)
                        .and(eventForm.privateId.eq(privateId))
                        .and(eventForm.status.eq(APPLIED)))
                .exists();
    }

    @Override
    public long countApprovedEventsByYear(int year) {
        Long result = queryFactory.select(event.count())
                .from(event)
                .where(event.isApprove.eq(true)
                        .and(event.startTime.year().eq(year)))
                .fetchOne();
        return result != null ? result : 0L;
    }

    @Override
    public double sumDistanceByYear(int year) {
        BigDecimal result = queryFactory.select(event.expectedRunningDistanceKm.sum())
                .from(event)
                .where(event.isApprove.eq(true)
                        .and(event.startTime.year().eq(year)))
                .fetchOne();
        return result != null ? result.doubleValue() : 0.0;
    }

    @Override
    public long countMyParticipation(String privateId) {
        Long result = queryFactory.select(eventForm.count())
                .from(eventForm)
                .join(event).on(eventForm.eventId.eq(event.id))
                .where(eventForm.privateId.eq(privateId)
                        .and(eventForm.status.eq(APPLIED))
                        .and(event.isApprove.eq(true))
                        .and(checkByPastDateTime()))
                .fetchOne();
        return result != null ? result : 0L;
    }

    @Override
    public double sumMyParticipationDistance(String privateId) {
        BigDecimal result = queryFactory.select(event.expectedRunningDistanceKm.sum())
                .from(eventForm)
                .join(event).on(eventForm.eventId.eq(event.id))
                .where(eventForm.privateId.eq(privateId)
                        .and(eventForm.status.eq(APPLIED))
                        .and(event.isApprove.eq(true))
                        .and(checkByPastDateTime()))
                .fetchOne();
        return result != null ? result.doubleValue() : 0.0;
    }

    private BooleanBuilder checkByTitle(String title) {
        if (title == null || title.isEmpty()) {
            return new BooleanBuilder();
        }
        return new BooleanBuilder(event.name.containsIgnoreCase(title).or(event.content.containsIgnoreCase(title)));
    }

    private BooleanBuilder checkByCityName(CityName cityName){
        if(cityName==null){
            return new BooleanBuilder();
        } else if(cityName.equals(CityName.BUSAN)) {
            return new BooleanBuilder(event.cityName.eq(CityName.BUSAN));
        } else{
            return new BooleanBuilder(event.cityName.eq(CityName.SEOUL));
        }
    }

    private BooleanBuilder checkByNotEndedDateTime() {
        return new BooleanBuilder(event.endTime.gt(EventTemporalStatusResolver.now()));
    }

    private BooleanBuilder checkByPastDateTime() {
        return new BooleanBuilder(event.endTime.lt(EventTemporalStatusResolver.now()));
    }

    private BooleanBuilder checkByPublicEvent() {
        return new BooleanBuilder(event.isPrivate.eq(false));
    }

    private BooleanBuilder checkByKind(EventRecruitStatus kind){
        if(kind==null){
            return new BooleanBuilder();
        } else if(kind.equals(RECRUIT_UPCOMING)){
            LocalDate today = EventTemporalStatusResolver.today();
            LocalDateTime now = EventTemporalStatusResolver.now();
            return new BooleanBuilder(event.recruitStatus.ne(RECRUIT_CLOSE)
                    .and(event.startTime.gt(now))
                    .and(event.recruitStartDate.gt(today)));
        } else if(kind.equals(RECRUIT_OPEN)){
            LocalDate today = EventTemporalStatusResolver.today();
            LocalDateTime now = EventTemporalStatusResolver.now();
            return new BooleanBuilder(event.recruitStatus.ne(RECRUIT_CLOSE)
                    .and(event.startTime.gt(now))
                    .and(event.recruitStartDate.loe(today))
                    .and(event.recruitEndDate.goe(today)));
        } else if(kind.equals(RECRUIT_CLOSE)){
            LocalDate today = EventTemporalStatusResolver.today();
            LocalDateTime now = EventTemporalStatusResolver.now();
            return new BooleanBuilder(event.recruitStatus.eq(RECRUIT_CLOSE)
                    .or(event.startTime.loe(now))
                    .or(event.recruitEndDate.lt(today)));
        } else if (kind.equals(RECRUIT_END)) {
            return checkByPastDateTime();
        } else if(kind.equals(RECRUIT_ALL)){
            return new BooleanBuilder();
        }
        return null;
    }

    private BooleanBuilder checkByType(EventType type){
        if(type==null){
            return new BooleanBuilder();
        } else if(type.equals(EventType.COMPETITION)){
            return new BooleanBuilder(event.type.eq(EventType.COMPETITION));
        } else if(type.equals(EventType.TRAINING)){
            return new BooleanBuilder(event.type.eq(EventType.TRAINING));
        }
        return null;
    }

}
