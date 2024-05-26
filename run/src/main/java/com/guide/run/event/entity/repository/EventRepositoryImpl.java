package com.guide.run.event.entity.repository;

import com.guide.run.admin.dto.EventDto;
import com.guide.run.event.entity.QEvent;
import com.guide.run.event.entity.QEventForm;
import com.guide.run.event.entity.dto.response.calender.MyEventOfDayOfCalendar;
import com.guide.run.event.entity.dto.response.calender.MyEventOfMonth;
import com.guide.run.event.entity.dto.response.get.MyEvent;
import com.guide.run.event.entity.dto.response.get.MyPageEvent;
import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.event.entity.type.EventType;
import com.guide.run.global.converter.TimeFormatter;
import com.guide.run.global.exception.UnknownException;
import com.guide.run.user.entity.user.QUser;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.guide.run.event.entity.QEvent.event;
import static com.guide.run.event.entity.QEventForm.eventForm;
import static com.guide.run.event.entity.type.EventRecruitStatus.*;
import static com.guide.run.user.entity.user.QUser.user;
import static java.time.temporal.ChronoUnit.DAYS;

public class EventRepositoryImpl implements EventRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public EventRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public long countMyEventAfterYear(String privateId, String kind, int year) {
        LocalDateTime fromDate = LocalDateTime.of(year, 1, 1, 0, 0, 0);
        long count = queryFactory.select(event.id)
                .from(event, eventForm)
                .where(
                        //privateId 조건 처리
                        eventForm.privateId.eq(privateId),
                        eventForm.eventId.eq(event.id),

                        //kind 조건 처리
                        searchByKind(kind),

                        //year 조건 처리
                        event.startTime.year().eq(year)

                )
                .fetch().size();

        return count;
    }

    @Override
    public List<MyPageEvent> findMyEventAfterYear(String privateId, int start, int limit, String kind, int year) {

        StringExpression formattedDate = Expressions.stringTemplate("FUNCTION('DATE_FORMAT', {0}, {1})"
                , event.startTime
                , ConstantImpl.create("%Y:%m:%d"));

        List<MyPageEvent> result = queryFactory
                .select(
                        Projections.constructor(MyPageEvent.class,
                                event.id.as("eventId"),
                                event.type.as("eventType"),
                                event.name.as("title"),
                                formattedDate.as("date"),
                                event.recruitStatus)
                )
                .from(event, eventForm)
                .where(
                        //privateId 조건 처리
                        eventForm.privateId.eq(privateId),
                        eventForm.eventId.eq(event.id),
                        //kind 조건 처리
                        searchByKind(kind),

                        //year 조건 처리
                        event.startTime.year().eq(year)

                )
                .orderBy(
                        event.startTime.desc()
                )
                .offset(start)
                .limit(limit)
                .fetch();

        return result;
    }

    private BooleanExpression searchByKind(String kind){
        if(kind.equals("RECRUIT_UPCOMING")){
            return event.recruitStatus.eq(EventRecruitStatus.RECRUIT_UPCOMING);
        } else if(kind.equals("RECRUIT_OPEN")){
            return event.recruitStatus.eq(RECRUIT_OPEN);
        } else if(kind.equals("RECRUIT_CLOSE")){
            return event.recruitStatus.eq(RECRUIT_CLOSE);
        } else if (kind.equals("RECRUIT_END")) {
            return event.recruitStatus.eq(RECRUIT_END);
        } else{
            return null;
        }
    }


    @Override
    public List<EventDto> sortAdminEvent(int start, int limit) {
        List<EventDto> results = queryFactory.select(
                Projections.constructor(EventDto.class,
                        event.id.as("eventId"),
                        event.name.as("title"),
                        event.startTime.as("smallDate"),
                        event.startTime,
                        user.name.as("organizer"),
                        user.recordDegree.as("pace"),
                        event.recruitStatus,
                        event.isApprove.as("approval"),
                        event.maxNumG.add(event.maxNumV).as("maxApply"),
                        event.maxNumV,
                        event.maxNumG,
                        event.updatedAt.as("update_date"),
                        event.updatedAt.as("update_time")

                        )
                )
                .from(event, user)
                .where(event.organizer.eq(user.privateId))
                .orderBy()
                .offset(start)
                .limit(limit)
                .fetch();
        return null;
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
                    .where(event.recruitStatus.eq(eventRecruitStatus))
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
                    .where(event.recruitStatus.ne(RECRUIT_END))
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
                .where(event.startTime.between(startTime, endTime))
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
                                event.startTime.as("endDate"),
                                event.recruitStatus.as("recruitStatus"))
                )
                .from(event)
                .join(eventForm).on(event.id.eq(eventForm.eventId),
                        eventForm.privateId.eq(privateId))
                .where(event.startTime.between(startTime, endTime))
                .orderBy(event.startTime.desc())
                .fetch();

        return fetch;
    }

    @Override
    public long getAllMyEventListCount(EventType eventType, EventRecruitStatus eventRecruitStatus, String privateId) {
        return queryFactory.select(event.id.as("eventId"))
                .from(event)
                .join(eventForm).on(event.id.eq(eventForm.eventId),
                        eventForm.privateId.eq(privateId))
                .where(checkByKind(eventRecruitStatus).and(checkByType(eventType)))
                .fetch().size();
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

    @Override
    public long sortAdminEventCount() {
        return 0;
    }
}
