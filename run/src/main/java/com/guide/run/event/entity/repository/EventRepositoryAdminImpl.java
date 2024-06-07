package com.guide.run.event.entity.repository;

import com.guide.run.admin.dto.EventDto;
import com.guide.run.admin.dto.EventHistoryDto;
import com.guide.run.admin.dto.condition.EventSortCond;
import com.guide.run.admin.dto.response.event.CurrentEventResponse;
import com.guide.run.event.entity.dto.response.get.MyPageEvent;
import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.event.entity.type.EventType;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.guide.run.event.entity.QEvent.event;
import static com.guide.run.event.entity.QEventForm.eventForm;
import static com.guide.run.event.entity.type.EventRecruitStatus.*;
import static com.guide.run.user.entity.user.QUser.user;

public class EventRepositoryAdminImpl implements EventRepositoryAdmin{
    private final JPAQueryFactory queryFactory;

    public EventRepositoryAdminImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public long countMyEventAfterYear(String privateId, String kind, int year) {
        long count = queryFactory.select(event.id)
                .from(event, eventForm)
                .where(
                        //privateId 조건 처리
                        eventForm.privateId.eq(privateId),
                        eventForm.eventId.eq(event.id),

                        //kind 조건 처리
                        searchByKind(kind),

                        //year 조건 처리
                        sortByYear(year)

                )
                .fetch().size();

        return count;
    }

    @Override
    public List<MyPageEvent> findMyEventAfterYear(String privateId, int start, int limit, String kind, int year) {

        StringExpression formattedDate = Expressions.stringTemplate("FUNCTION('DATE_FORMAT', {0}, {1})"
                , event.startTime
                , ConstantImpl.create("%Y-%m-%d"));

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
                        sortByYear(year)
                )
                .orderBy(
                        event.startTime.desc()
                )
                .offset(start)
                .limit(limit)
                .fetch();

        return result;
    }


    @Override
    public List<EventDto> getAdminEventList(int start, int limit, EventSortCond cond) {
        List<EventDto> results = queryFactory.select(
                        Projections.constructor(EventDto.class,
                                event.id.as("eventId"),
                                event.name.as("name"),
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
                .orderBy(createOrderSpec(cond))
                .offset(start)
                .limit(limit)
                .fetch();
        return results;
    }

    @Override
    public long getAdminEventCount() {

        long count = queryFactory.select(event.id)
                .from(event, user)
                .where(event.organizer.eq(user.privateId))
                .orderBy()
                .fetch().size();
        return count;
    }

    @Override
    public List<CurrentEventResponse> findCurrentEvent(int start, int limit) {
        List<CurrentEventResponse> results = queryFactory
                .select(Projections.constructor(CurrentEventResponse.class,
                        event.id.as("eventId"),
                        event.type.as("eventType"),
                        event.name.as("title"),
                        event.recruitStatus
                ))
                .from(event)
                .where(event.recruitStatus.ne(RECRUIT_END))
                .orderBy(event.createdAt.desc())
                .offset(start)
                .limit(limit)
                .fetch();

        return results;
    }

    @Override
    public List<EventHistoryDto> getUserEventHistory( String privateId, int start, int limit, String sort) {
        StringExpression formattedDate = Expressions.stringTemplate("FUNCTION('DATE_FORMAT', {0}, {1})"
                , event.startTime
                , ConstantImpl.create("%Y-%m-%d"));

        List<EventHistoryDto> results = queryFactory
                .select(Projections.constructor(EventHistoryDto.class,
                        event.id.as("eventId"),
                        event.type.as("eventType"),
                        event.name.as("title"),
                        formattedDate.as("startDate"),
                        event.recruitStatus))
                .from(event, eventForm)
                .where(eventForm.privateId.eq(privateId),
                        eventForm.eventId.eq(event.id),
                        eventHistoryCond(sort))
                .orderBy(event.createdAt.desc())
                .offset(start)
                .limit(limit)
                .fetch();
        return results;
    }

    @Override
    public long getUserEventHistoryCount( String privateId, String sort) {

        long count = queryFactory
                .select(event.id)
                .from(event, eventForm)
                .where(eventForm.privateId.eq(privateId),
                        eventForm.eventId.eq(event.id),
                        eventHistoryCond(sort))
                .fetch().size();
        return count;
    }

    @Override
    public List<EventDto> searchAdminEvent(String text, int start, int limit, EventSortCond cond) {
        List<EventDto> results = queryFactory.select(
                        Projections.constructor(
                                EventDto.class,
                                event.id.as("eventId"),
                                event.name.as("name"),
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
                .where(
                        event.organizer.eq(user.privateId),
                        (event.name.contains(text)
                                .or(user.name.contains(text))
                                .or(user.snsId.toUpperCase().contains(text.toUpperCase()))
                                .or(user.recordDegree.toUpperCase().contains(text.toUpperCase()))
                                //기타 검색 조건 추가 필요
                                )
                )
                .orderBy(createOrderSpec(cond))
                .offset(start)
                .limit(limit)
                .fetch();

        return results;
    }

    @Override
    public long searchAdminEventCount(String text) {
        long count = queryFactory.select(event.id)
                .from(event, user)
                .where(
                        event.organizer.eq(user.privateId),
                        (event.name.contains(text)
                                .or(user.name.contains(text))
                                .or(user.snsId.toUpperCase().contains(text.toUpperCase()))
                                .or(user.recordDegree.toUpperCase().contains(text.toUpperCase()))
                                //todo : 기타 검색 조건 추가 필요
                        )
                )
                .fetch().size();
        return count;
    }

    @Override
    public List<EventHistoryDto> searchUserEventHistory(String privateId, String text, int start, int limit) {
        StringExpression formattedDate = Expressions.stringTemplate("FUNCTION('DATE_FORMAT', {0}, {1})"
                , event.startTime
                , ConstantImpl.create("%Y-%m-%d"));

        List<EventHistoryDto> results = queryFactory
                .select(Projections.constructor(EventHistoryDto.class,
                        event.id.as("eventId"),
                        event.type.as("eventType"),
                        event.name.as("title"),
                        formattedDate.as("startDate"),
                        event.recruitStatus))
                .from(event, eventForm)
                .where(eventForm.privateId.eq(privateId),
                        eventForm.eventId.eq(event.id),
                        event.isApprove.ne(false),
                        (event.name.contains(text)
                                //todo : 기타 검색 조건 추가 필요

                                )
                        )
                .orderBy(event.createdAt.desc())
                .offset(start)
                .limit(limit)
                .fetch();
        return results;
    }

    @Override
    public long searchUserEventHistoryCount(String privateId, String text) {

        long count = queryFactory
                .select(event.id)
                .from(event, eventForm)
                .where(eventForm.privateId.eq(privateId),
                        eventForm.eventId.eq(event.id),
                        event.isApprove.ne(false),
                        (event.name.contains(text)
                                //todo : 기타 검색 조건 추가 필요
                        )
                )
                .fetch().size();
        return count;
    }

    private BooleanExpression eventHistoryCond(String kind){
        if(kind.equals("COMPETITION")){
            return event.type.eq(EventType.COMPETITION);
        } else if (kind.equals("TRAINING")) {
            return event.type.eq(EventType.TRAINING);
        }else{
            return null;
        }
    }

    private OrderSpecifier[] createOrderSpec(EventSortCond cond) {
        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();

        if (cond.isApproval()) {
            orderSpecifiers.add(new OrderSpecifier(Order.ASC, event.isApprove));
        }

        if(cond.isName()){
            orderSpecifiers.add(new OrderSpecifier<>(Order.ASC, event.name));
        }

        if (cond.isTime()) {
            orderSpecifiers.add(new OrderSpecifier(Order.ASC, event.updatedAt));
        }

        if (cond.isOrganizer()) {
            orderSpecifiers.add(new OrderSpecifier(Order.ASC, user.name));
        }

        if (!cond.isApproval()) {
            orderSpecifiers.add(new OrderSpecifier(Order.DESC, event.isApprove));
        }

        if(!cond.isName()){
            orderSpecifiers.add(new OrderSpecifier<>(Order.DESC, event.name));
        }

        if (!cond.isTime()) {
            orderSpecifiers.add(new OrderSpecifier(Order.DESC, event.updatedAt));
        }

        if (!cond.isOrganizer()) {
            orderSpecifiers.add(new OrderSpecifier(Order.DESC, user.name));
        }

        return orderSpecifiers.toArray(new OrderSpecifier[orderSpecifiers.size()]);
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

    private BooleanExpression sortByYear(int year){
        if(year==0){
            return null;
        }else{
            //year 조건 처리
            return event.startTime.year().goe(year);
        }
    }

}
