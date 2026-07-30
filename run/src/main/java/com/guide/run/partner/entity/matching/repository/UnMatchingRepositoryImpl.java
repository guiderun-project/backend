package com.guide.run.partner.entity.matching.repository;

import com.guide.run.event.entity.dto.response.match.MatchingWaitingFlatDto;
import com.guide.run.event.entity.type.EventFormStatus;
import com.guide.run.user.entity.type.UserType;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.List;

import static com.guide.run.event.entity.QEventForm.eventForm;
import static com.guide.run.partner.entity.matching.QUnMatching.unMatching;
import static com.guide.run.user.entity.user.QUser.*;

public class UnMatchingRepositoryImpl implements UnMatchingRepositoryCustom
{
    private final JPAQueryFactory queryFactory;

    public UnMatchingRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public long getUserTypeCount(Long eventId, UserType userType){
        return queryFactory.select(user.privateId)
                .from(unMatching)
                .join(user).on(unMatching.privateId.eq(user.privateId))
                .where(unMatching.eventId.eq(eventId).and(user.type.eq(userType)))
                .fetch().size();
    }

    @Override
    public List<MatchingWaitingFlatDto> findWaitingParticipants(Long eventId) {
        return queryFactory.select(Projections.constructor(MatchingWaitingFlatDto.class,
                        user.userId.as("userId"),
                        user.name.as("name"),
                        user.type.as("type"),
                        eventForm.hopeTeam.as("hopeTeam"),
                        eventForm.hopePartner.as("hopePartner"),
                        eventForm.referContent.as("referContent"),
                        user.trainingCnt.as("trainingCnt"),
                        user.competitionCnt.as("competitionCnt")))
                .from(unMatching)
                .join(user).on(unMatching.privateId.eq(user.privateId))
                .join(eventForm).on(unMatching.privateId.eq(eventForm.privateId)
                        .and(eventForm.eventId.eq(eventId))
                        .and(eventForm.status.eq(EventFormStatus.APPLIED)))
                .where(unMatching.eventId.eq(eventId))
                .orderBy(eventForm.hopeTeam.asc(), user.name.asc())
                .fetch();
    }
}
