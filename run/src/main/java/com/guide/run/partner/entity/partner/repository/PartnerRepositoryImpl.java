package com.guide.run.partner.entity.partner.repository;

import com.guide.run.partner.entity.partner.Partner;
import com.guide.run.user.entity.type.UserType;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.List;

import static com.guide.run.partner.entity.partner.QPartner.partner;

public class PartnerRepositoryImpl implements PartnerRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public PartnerRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Partner> findActivityPartners(String privateId, UserType userType, String sort, int page, int size) {
        return queryFactory.selectFrom(partner)
                .where(getUserTypeForActivity(userType, privateId), getPartnerKind("all"))
                .orderBy("OLD".equals(sort) ? partner.updatedAt.asc() : partner.updatedAt.desc())
                .offset((long) page * size)
                .limit(size)
                .fetch();
    }

    @Override
    public long countActivityPartners(String privateId, UserType userType) {
        Long result = queryFactory.select(partner.count())
                .from(partner)
                .where(getUserTypeForActivity(userType, privateId), getPartnerKind("all"))
                .fetchOne();
        return result != null ? result : 0L;
    }

    private BooleanExpression getUserTypeForActivity(UserType type, String privateId) {
        return type.equals(UserType.GUIDE)
                ? partner.guideId.eq(privateId)
                : partner.viId.eq(privateId);
    }

    /**
     * 파트너의 종류 조건:
     * - "COMPETITON": contestIds에 값이 있는 경우
     * - "TRAINING": trainingIds에 값이 있는 경우
     * - 그 외("all" 또는 null): 두 컬럼 중 하나라도 값이 있으면.
     */
    private BooleanExpression getPartnerKind(String kind) {
        if ("COMPETITON".equals(kind)) {
            return Expressions.booleanTemplate("(COALESCE(LENGTH({0}), 0) > 0)", partner.contestIds);
        } else if ("TRAINING".equals(kind)) {
            return Expressions.booleanTemplate("(COALESCE(LENGTH({0}), 0) > 0)", partner.trainingIds);
        } else {
            return Expressions.booleanTemplate(
                    "(COALESCE(LENGTH({0}), 0) > 0 or COALESCE(LENGTH({1}), 0) > 0)",
                    partner.trainingIds, partner.contestIds
            );
        }
    }

}
