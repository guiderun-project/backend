package com.guide.run.partner.entity.matching.repository;

import com.guide.run.attendance.entity.QAttendance;
import com.guide.run.event.entity.QEventForm;
import com.guide.run.event.entity.dto.response.match.MatchedGuideInfo;
import com.guide.run.event.entity.dto.response.match.MatchedViInfo;
import com.guide.run.event.entity.dto.response.match.MatchingCompletedFlatDto;
import com.guide.run.user.entity.type.UserType;
import com.guide.run.user.entity.user.QUser;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.List;

import static com.guide.run.attendance.entity.QAttendance.attendance;
import static com.guide.run.event.entity.QEventForm.eventForm;
import static com.guide.run.partner.entity.matching.QMatching.matching;
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
                eventForm.hopeTeam.as("applyRecord"),
                        attendance.isAttend.as("isAttended"),
                user.recordDegree.as("recordDegree")))
                .from(matching)
                .where(matching.eventId.eq(eventId).and(matching.viId.eq(viId)))
                .join(user).on(user.privateId.eq(matching.guideId))
                .join(eventForm).on(user.privateId.eq(eventForm.privateId).and(eventForm.eventId.eq(eventId)))
                .join(attendance).on(user.privateId.eq(attendance.privateId).and(attendance.eventId.eq(eventId)))
                .orderBy(user.name.asc())
                .fetch();
    }

    @Override
    public List<MatchedViInfo> findAllMatchedViByEventIdAndUserType(Long eventId, UserType userType) {
        return queryFactory.select(Projections.constructor(MatchedViInfo.class,
                        user.userId.as("userId"),
                        user.type.as("type"),
                        user.name.as("name"),
                        eventForm.hopeTeam.as("applyRecord"),
                        attendance.isAttend.as("isAttended"),
                        user.recordDegree.as("recordDegree")))
                .from(matching)
                .join(user).on(user.privateId.eq(matching.viId))
                .join(eventForm).on(user.privateId.eq(eventForm.privateId).and(eventForm.eventId.eq(eventId)))
                .join(attendance).on(user.privateId.eq(attendance.privateId).and(attendance.eventId.eq(eventId)))
                .where(matching.eventId.eq(eventId).and(user.type.eq(userType)))
                .orderBy(user.name.asc())
                .distinct()
                .fetch();
    }

    @Override
    public List<MatchingCompletedFlatDto> findMatchingCompletedByEventId(Long eventId) {
        QUser viUser = new QUser("viUser");
        QUser guideUser = new QUser("guideUser");
        QEventForm viForm = new QEventForm("viForm");
        QEventForm guideForm = new QEventForm("guideForm");
        QAttendance viAttendance = new QAttendance("viAttendance");
        QAttendance guideAttendance = new QAttendance("guideAttendance");

        return queryFactory.select(Projections.constructor(MatchingCompletedFlatDto.class,
                        viUser.userId.as("viUserId"),
                        viUser.type.as("viType"),
                        viUser.name.as("viName"),
                        viForm.hopeTeam.as("viApplyRecord"),
                        viAttendance.isAttend.as("viIsAttended"),
                        viUser.recordDegree.as("viRecordDegree"),
                        viForm.hopeTeam.as("viRunningGroup"),
                        guideUser.userId.as("guideUserId"),
                        guideUser.type.as("guideType"),
                        guideUser.name.as("guideName"),
                        guideForm.hopeTeam.as("guideApplyRecord"),
                        guideAttendance.isAttend.as("guideIsAttended"),
                        guideUser.recordDegree.as("guideRecordDegree")))
                .from(matching)
                .join(viUser).on(viUser.privateId.eq(matching.viId))
                .join(viForm).on(viForm.privateId.eq(matching.viId).and(viForm.eventId.eq(eventId)))
                .join(viAttendance).on(viAttendance.privateId.eq(matching.viId).and(viAttendance.eventId.eq(eventId)))
                .join(guideUser).on(guideUser.privateId.eq(matching.guideId))
                .join(guideForm).on(guideForm.privateId.eq(matching.guideId).and(guideForm.eventId.eq(eventId)))
                .join(guideAttendance).on(guideAttendance.privateId.eq(matching.guideId).and(guideAttendance.eventId.eq(eventId)))
                .where(matching.eventId.eq(eventId))
                .orderBy(viForm.hopeTeam.asc(), viUser.name.asc(), guideUser.name.asc())
                .fetch();
    }
}
