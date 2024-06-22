package com.guide.run.partner.entity.partner.repository;

import com.guide.run.partner.entity.partner.QPartnerLike;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import static com.guide.run.partner.entity.partner.QPartnerLike.partnerLike;

public class PartnerLikeRepositoryImpl implements PartnerLikeRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    public PartnerLikeRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public long countPartnerLikeNum(String partnerId) {
        Long num = queryFactory.select(partnerLike.count())
                .from(partnerLike)
                .where(partnerLike.recId.eq(partnerId))
                .fetchOne();


        return num;
    }
    
}
