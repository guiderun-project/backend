package com.guide.run.partner.entity.matching.repository;

import com.guide.run.event.entity.dto.response.match.NotMatchUserInfo;
import com.guide.run.partner.entity.matching.QUnMatching;
import com.guide.run.user.entity.type.UserType;
import com.guide.run.user.entity.user.QUser;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.List;

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
    public List<NotMatchUserInfo> findNotMatchUserInfos(Long eventId) {
        return queryFactory.select(Projections.constructor(NotMatchUserInfo.class,
                user.userId.as("userId"),
                user.type.as("type"),
                user.name.as("name")))
                .from(unMatching)
                .join(user).on(unMatching.privateId.eq(user.privateId))
                .where(unMatching.eventId.eq(eventId))
                .fetch();
    }
}
