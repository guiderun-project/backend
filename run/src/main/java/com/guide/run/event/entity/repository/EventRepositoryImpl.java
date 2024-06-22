package com.guide.run.event.entity.repository;

import com.guide.run.admin.dto.EventDto;
import com.guide.run.event.entity.Event;

import com.guide.run.event.entity.QEvent;
import com.guide.run.event.entity.QEventForm;
import com.guide.run.event.entity.dto.response.calender.MyEventOfDayOfCalendar;
import com.guide.run.event.entity.dto.response.calender.MyEventOfMonth;
import com.guide.run.event.entity.dto.response.get.*;
import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.event.entity.type.EventStatus;
import com.guide.run.event.entity.type.EventType;
import com.guide.run.global.converter.TimeFormatter;
import com.guide.run.global.exception.UnknownException;
import com.guide.run.partner.entity.matching.QMatching;
import com.guide.run.partner.entity.partner.QPartner;
import com.guide.run.user.entity.user.QUser;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.guide.run.event.entity.QEvent.event;
import static com.guide.run.event.entity.QEventForm.eventForm;
import static com.guide.run.event.entity.type.EventRecruitStatus.*;
import static com.guide.run.partner.entity.matching.QMatching.matching;
import static com.guide.run.partner.entity.partner.QPartner.partner;
import static com.guide.run.user.entity.user.QUser.user;
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
                            event.endTime.as("endDate"))
                    )
                    .from(event)
                    .join(eventForm).on(event.id.eq(eventForm.eventId),
                            eventForm.privateId.eq(privateId))
                    .where(event.recruitStatus.eq(eventRecruitStatus).and(event.isApprove.eq(true)))
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
                                    event.endTime.as("endDate"))
                    )
                    .from(event)
                    .join(eventForm).on(event.id.eq(eventForm.eventId),
                            eventForm.privateId.eq(privateId))
                    .where(event.recruitStatus.ne(RECRUIT_END).and(event.isApprove.eq(true)))
                    .orderBy(event.endTime.desc())
                    .offset(0)
                    .limit(4)
                    .fetch();
            for(MyEvent myEvent : fetch){
                myEvent.setdDay((int)DAYS.between(LocalDate.now(),myEvent.getEndDate()));
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
                                event.recruitStatus.as("recruitStatus"))
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
    public long getAllMyEventListCount(EventType eventType, EventRecruitStatus eventRecruitStatus, String privateId) {
        return queryFactory.select(event.count())
                .from(event)
                .join(eventForm).on(eventForm.eventId.eq(event.id))
                .where(checkByKind(eventRecruitStatus).and(checkByType(eventType)).and(event.isApprove.eq(true))
                        .and(eventForm.privateId.eq(privateId)))
                .fetchOne();
    }

    @Override
    public List<AllEvent> getAllMyEventList(int limit, int start, EventType eventType, EventRecruitStatus eventRecruitStatus, String privateId) {
        return queryFactory.select(Projections.constructor(AllEvent.class,
                event.id.as("eventId"),
                event.type.as("eventType"),
                event.name.as("name"),
                event.startTime.as("date"),
                event.recruitStatus.as("recruitStatus")))
                .from(event)
                .join(eventForm).on(eventForm.eventId.eq(event.id))
                .where(checkByKind(eventRecruitStatus).and(checkByType(eventType)).and(event.isApprove.eq(true))
                        .and(eventForm.privateId.eq(privateId)))
                .orderBy(event.startTime.asc())
                .offset(start)
                .limit(limit)
                .fetch();
    }

    @Override
    public List<MyEventDday> getMyEventDday(String userId) {
        return queryFactory.select(Projections.constructor(MyEventDday.class,
                event.name.as("name"),
                event.startTime.as("dDay")))
                .from(event)
                .join(eventForm).on(event.id.eq(eventForm.eventId),
                        eventForm.privateId.eq(userId))
                .where(event.recruitStatus.ne(RECRUIT_END).and(event.isApprove.eq(true)))
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
    public List<AllEvent> getAllEventList(int limit, int start, EventType eventType, EventRecruitStatus eventRecruitStatus) {
        return queryFactory.select(Projections.constructor(AllEvent.class,
                        event.id.as("eventId"),
                        event.type.as("eventType"),
                        event.name.as("name"),
                        event.startTime.as("date"),
                        event.recruitStatus.as("recruitStatus")))
                .from(event)
                .where(checkByKind(eventRecruitStatus).and(checkByType(eventType)).and(event.isApprove.eq(true)))
                .orderBy(event.startTime.asc())
                .offset(start)
                .limit(limit)
                .fetch();
    }



    private BooleanBuilder checkByKind(EventRecruitStatus kind){
        if(kind==null){
            return new BooleanBuilder();
        } else if(kind.equals(RECRUIT_UPCOMING)){
            return new BooleanBuilder(event.recruitStatus.eq(EventRecruitStatus.RECRUIT_UPCOMING));
        } else if(kind.equals(RECRUIT_OPEN)){
            return new BooleanBuilder(event.recruitStatus.eq(RECRUIT_OPEN));
        } else if(kind.equals(RECRUIT_CLOSE)){
            return new BooleanBuilder(event.recruitStatus.eq(RECRUIT_CLOSE));
        } else if (kind.equals(RECRUIT_END)) {
            return new BooleanBuilder(event.recruitStatus.eq(RECRUIT_END));
        } else if(kind.equals(RECRUIT_ALL)){
            return new BooleanBuilder(event.recruitStatus.ne(RECRUIT_END));
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

    private BooleanBuilder checkByPrivateId(String privateId){
        if(privateId==null){
            return new BooleanBuilder();
        } else {
            return new BooleanBuilder(eventForm.privateId.eq(privateId));
        }
    }

}
