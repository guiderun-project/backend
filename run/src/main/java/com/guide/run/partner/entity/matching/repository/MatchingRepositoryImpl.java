package com.guide.run.partner.entity.matching.repository;

import com.guide.run.attendance.entity.QAttendance;
import com.guide.run.event.entity.QEventForm;
import com.guide.run.event.entity.dto.response.match.MatchingCompletedFlatDto;
import com.guide.run.event.entity.type.EventFormStatus;
import com.guide.run.user.entity.type.UserType;
import com.guide.run.user.entity.user.QUser;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.List;

import static com.guide.run.partner.entity.matching.QMatching.matching;

public class MatchingRepositoryImpl implements MatchingRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public MatchingRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }


    @Override
    public long countDistinctViByEventId(Long eventId) {
        QUser matchedVi = new QUser("matchedVi");
        QEventForm matchedViForm = new QEventForm("matchedViForm");
        QAttendance matchedViAttendance = new QAttendance("matchedViAttendance");

        Long count = queryFactory.select(matching.viId.countDistinct())
                .from(matching)
                .join(matchedVi).on(matchedVi.privateId.eq(matching.viId))
                .join(matchedViForm).on(matchedViForm.privateId.eq(matching.viId)
                        .and(matchedViForm.eventId.eq(eventId))
                        .and(matchedViForm.status.eq(EventFormStatus.APPLIED)))
                .join(matchedViAttendance).on(matchedViAttendance.privateId.eq(matching.viId)
                        .and(matchedViAttendance.eventId.eq(eventId)))
                .where(matching.eventId.eq(eventId).and(matchedVi.type.eq(UserType.VI)))
                .fetchOne();
        return count != null ? count : 0L;
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
                .join(viForm).on(viForm.privateId.eq(matching.viId)
                        .and(viForm.eventId.eq(eventId))
                        .and(viForm.status.eq(EventFormStatus.APPLIED)))
                .join(viAttendance).on(viAttendance.privateId.eq(matching.viId).and(viAttendance.eventId.eq(eventId)))
                .join(guideUser).on(guideUser.privateId.eq(matching.guideId))
                .join(guideForm).on(guideForm.privateId.eq(matching.guideId)
                        .and(guideForm.eventId.eq(eventId))
                        .and(guideForm.status.eq(EventFormStatus.APPLIED)))
                .join(guideAttendance).on(guideAttendance.privateId.eq(matching.guideId).and(guideAttendance.eventId.eq(eventId)))
                .where(matching.eventId.eq(eventId))
                .orderBy(viForm.hopeTeam.asc(), viUser.name.asc(), guideUser.name.asc())
                .fetch();
    }
}
