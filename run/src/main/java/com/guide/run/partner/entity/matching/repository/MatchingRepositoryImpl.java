package com.guide.run.partner.entity.matching.repository;

import com.guide.run.event.entity.dto.response.match.MatchedGuideInfo;
import com.guide.run.event.entity.dto.response.match.MatchedViInfo;
import com.guide.run.user.entity.type.UserType;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.List;

import static com.guide.run.partner.entity.matching.QMatching.matching;
import static com.guide.run.partner.entity.matching.QUnMatching.unMatching;
import static com.guide.run.temp.member.entity.QAttendance.attendance;
import static com.guide.run.user.entity.user.QUser.user;

public class MatchingRepositoryImpl implements MatchingRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public MatchingRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }


    @Override
    public List<MatchedGuideInfo> findAllMatchedGuideByEventIdAndViId(Long eventId, String viId) {
        return queryFactory.select(Projections.constructor(MatchedGuideInfo.class,
                user.userId.as("userId"),
                user.type.as("type"),
                user.name.as("name"),
                attendance.isAttend.as("isAttended")))
                .from(matching)
                .join(user).on(user.userId.eq(matching.guideId))
                .join(attendance).on(matching.guideId.eq(attendance.privateId).and(matching.eventId.eq(eventId)))
                .where(matching.eventId.eq(eventId).and(matching.viId.eq(viId)))
                .fetch();
    }

    @Override
    public List<MatchedViInfo> findAllMatchedViByEventIdAndUserType(Long eventId, UserType userType) {
        return queryFactory.select(Projections.constructor(MatchedViInfo.class,
                        user.userId.as("userId"),
                        user.type.as("type"),
                        user.name.as("name"),
                        attendance.isAttend.as("isAttended")))
                .from(matching)
                .join(user).on(user.userId.eq(matching.viId))
                .join(attendance).on(matching.viId.eq(attendance.privateId).and(matching.eventId.eq(eventId)))
                .where(matching.eventId.eq(eventId).and(user.type.eq(userType)))
                .groupBy(matching.viId)
                .fetch();
    }
}
