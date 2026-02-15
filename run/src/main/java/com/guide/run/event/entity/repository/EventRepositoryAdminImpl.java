package com.guide.run.event.entity.repository;

import com.guide.run.admin.dto.EventDto;
import com.guide.run.admin.dto.EventHistoryDto;
import com.guide.run.admin.dto.condition.EventSortCond;
import com.guide.run.admin.dto.response.Guide1365Response;
import com.guide.run.admin.dto.response.event.CurrentEventResponse;
import com.guide.run.event.entity.dto.response.get.MyPageEvent;
import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.event.entity.type.EventType;
import com.guide.run.user.entity.type.UserType;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.guide.run.attendance.entity.QAttendance.attendance;
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
        long count = queryFactory.select(event.count())
                .from(event)
                .join(eventForm).on(eventForm.eventId.eq(event.id))
                .where(
                        //privateId 조건 처리
                        eventForm.privateId.eq(privateId),
                        eventForm.eventId.eq(event.id),

                        //kind 조건 처리
                        searchByKind(kind),

                        //year 조건 처리
                        sortByYear(year)

                )
                .fetchOne();

        return count;
    }

    @Override
    public List<MyPageEvent> findMyEventAfterYear(String privateId, int start, int limit, String kind, int year) {

        List<MyPageEvent> result = queryFactory
                .select(
                        Projections.constructor(MyPageEvent.class,
                                event.id.as("eventId"),
                                event.type.as("eventType"),
                                event.name.as("title"),
                                formattedDate(event.startTime).as("date"),
                                event.recruitStatus)
                )
                .from(event)
                .join(eventForm).on(eventForm.eventId.eq(event.id))
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

        StringExpression smallDate = Expressions.stringTemplate("FUNCTION('DATE_FORMAT', {0}, {1})"
        ,event.startTime
        ,ConstantImpl.create("%c/%e"));

        List<EventDto> results = queryFactory.select(
                        Projections.constructor(
                                EventDto.class,
                                event.id.as("eventId"),
                                event.name.as("name"),
                                smallDate.as("smallDate"),
                                formattedDateTime(event.startTime).as("startTime"),
                                user.name.as("organizer"),
                                user.recordDegree.as("pace"),
                                event.recruitStatus,
                                event.isApprove.as("approval"),
                                event.maxNumV.as("minNumV"),
                                event.maxNumG.as("minNumG"),
                                formattedDate(event.createdAt).as("update_date"),
                                formattedTime(event.createdAt).as("update_time")
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

        long count = queryFactory.select(event.count())
                .from(event, user)
                .where(event.organizer.eq(user.privateId))
                .orderBy()
                .fetchOne();
        return count;
    }

    @Override
    public List<CurrentEventResponse> findCurrentEvent(int start, int limit) {
        List<CurrentEventResponse> results = queryFactory
                .select(Projections.constructor(CurrentEventResponse.class,
                        event.id.as("eventId"),
                        event.type.as("eventType"),
                        event.name.as("title"),
                        formattedDate(event.startTime).as("date"),
                        event.recruitStatus
                ))
                .from(event)
                .where(event.recruitStatus.ne(RECRUIT_END))
                .orderBy(event.startTime.desc())
                .offset(start)
                .limit(limit)
                .fetch();

        return results;
    }

    @Override
    public List<EventHistoryDto> getUserEventHistory( String privateId, int start, int limit, String kind) {

        List<EventHistoryDto> results = queryFactory
                .select(Projections.constructor(EventHistoryDto.class,
                        event.id.as("eventId"),
                        event.type.as("eventType"),
                        event.name.as("name"),
                        formattedDate(event.startTime).as("startDate"),
                        event.recruitStatus))
                .from(event)
                .join(eventForm).on(eventForm.eventId.eq(event.id))
                .where(eventForm.privateId.eq(privateId),
                        eventForm.eventId.eq(event.id),
                        eventHistoryCond(kind))
                .orderBy(event.createdAt.desc())
                .offset(start)
                .limit(limit)
                .fetch();
        return results;
    }

    @Override
    public long getUserEventHistoryCount( String privateId, String kind) {

        long count = queryFactory
                .select(event.count())
                .from(event)
                .join(eventForm).on(eventForm.eventId.eq(event.id))
                .where(eventForm.privateId.eq(privateId),
                        eventForm.eventId.eq(event.id),
                        eventHistoryCond(kind))
                .fetchOne();
        return count;
    }

    @Override
    public List<EventDto> searchAdminEvent(String text, int start, int limit, EventSortCond cond) {
        StringExpression smallDate = Expressions.stringTemplate("FUNCTION('DATE_FORMAT', {0}, {1})"
                ,event.startTime
                ,ConstantImpl.create("%c/%e"));
        List<EventDto> results = queryFactory.select(
                        Projections.constructor(
                                EventDto.class,
                                event.id.as("eventId"),
                                event.name.as("name"),
                                smallDate.as("smallDate"),
                                formattedDateTime(event.startTime).as("startTime"),
                                user.name.as("organizer"),
                                user.recordDegree.as("pace"),
                                event.recruitStatus,
                                event.isApprove.as("approval"),
                                event.maxNumV.as("minNumV"),
                                event.maxNumG.as("minNumG"),
                                formattedDate(event.createdAt).as("update_date"),
                                formattedTime(event.createdAt).as("update_time")
                        )
                )
                .from(event, user)
                .where(
                        event.organizer.eq(user.privateId),
                        (event.name.contains(text)
                                .or(user.name.contains(text))
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
        long count = queryFactory.select(event.count())
                .from(event, user)
                .where(
                        event.organizer.eq(user.privateId),
                        (event.name.contains(text)
                                .or(user.name.contains(text))
                                .or(user.recordDegree.toUpperCase().contains(text.toUpperCase()))
                                //todo : 기타 검색 조건 추가 필요
                        )
                )
                .fetchOne();
        return count;
    }

    @Override
    public List<EventHistoryDto> searchUserEventHistory(String privateId, String text, int start, int limit) {

        List<EventHistoryDto> results = queryFactory
                .select(Projections.constructor(EventHistoryDto.class,
                        event.id.as("eventId"),
                        event.type.as("eventType"),
                        event.name.as("name"),
                        formattedDate(event.startTime).as("startDate"),
                        event.recruitStatus))
                .from(event)
                .leftJoin(eventForm).on(eventForm.eventId.eq(event.id))
                .where(eventForm.eventId.eq(event.id),
                        eventForm.privateId.eq(privateId),
                        event.isApprove.ne(false),
                        (event.name.contains(text))
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
                .select(event.count())
                .from(event)
                .join(eventForm).on(eventForm.eventId.eq(event.id))
                .where(
                        eventForm.privateId.eq(privateId),
                        eventForm.eventId.eq(event.id),
                        event.isApprove.ne(false),
                        (event.name.contains(text))
                )
                .fetchOne();
        return count;
    }

    @Override
    public List<Guide1365Response> getGuide1365(long eventId) {

        List<Guide1365Response> responses = queryFactory
                .select(Projections.constructor(Guide1365Response.class,
                        user.name.as("name"),
                        user.id1365.as("id1365"),
                        user.phoneNumber.as("phone"),
                        user.birth.as("birth"))
                )
                .from(user, attendance)
                .where(user.type.eq(UserType.GUIDE),
                        attendance.isAttend.eq(true),
                        attendance.eventId.eq(eventId),
                        user.privateId.eq(attendance.privateId))
                .fetch();

        return responses;
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

    private OrderSpecifier<?>[] createOrderSpec(EventSortCond cond) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();


        if (cond.getTime()==0) {
            orderSpecifiers.add(new OrderSpecifier<>(Order.DESC, event.createdAt));
        }

        if (cond.getOrganizer()==0) {
            orderSpecifiers.add(new OrderSpecifier<>(Order.DESC, user.name));
        }

        if (cond.getApproval()==0) {
            orderSpecifiers.add(new OrderSpecifier<>(Order.DESC, event.isApprove));
        }

        if(cond.getName()==0){
            orderSpecifiers.add(new OrderSpecifier<>(Order.DESC, event.name));
        }

        if (cond.getApproval()==1) {
            orderSpecifiers.add(new OrderSpecifier<>(Order.ASC, event.isApprove));
        }

        if(cond.getName()==1){
            orderSpecifiers.add(new OrderSpecifier<>(Order.ASC, event.name));
        }

        if (cond.getTime()==1) {
            orderSpecifiers.add(new OrderSpecifier<>(Order.ASC, event.createdAt));
        }

        if (cond.getOrganizer()==1) {
            orderSpecifiers.add(new OrderSpecifier<>(Order.ASC, user.name));
        }

        if(cond.getOrganizer()==2 && cond.getTime()==2 && cond.getApproval()==2 && cond.getName()==2){
            orderSpecifiers.add(new OrderSpecifier<>(Order.DESC, event.createdAt));
        }

        return orderSpecifiers.toArray(new OrderSpecifier<?>[0]);
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
            // startTime에서 연도를 추출하는 NumberTemplate
            NumberTemplate<Integer> yearTemplate = Expressions.numberTemplate(Integer.class, "YEAR({0})", event.startTime);

            // 연도 비교 조건
            return yearTemplate.eq(year);
        }
    }

    private StringExpression formattedDate(DateTimePath<LocalDateTime> localDateTime){
        return Expressions.stringTemplate("FUNCTION('DATE_FORMAT', {0}, {1})"
                , localDateTime
                , ConstantImpl.create("%Y-%m-%d"));
    }

    private StringExpression formattedDateTime(DateTimePath<LocalDateTime> localDateTime){
        return Expressions.stringTemplate(
                "FUNCTION('DATE_FORMAT', {0}, {1})",
                localDateTime,
                ConstantImpl.create("%Y-%m-%d %H:%i:%s")
        );
    }

    private StringExpression formattedTime(DateTimePath<LocalDateTime> localDateTime){
        return Expressions.stringTemplate("FUNCTION('TIME_FORMAT', {0}, {1})"
                , localDateTime
                , ConstantImpl.create("%H:%i:%s"));
    }

}
