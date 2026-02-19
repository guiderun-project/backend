package com.guide.run.partner.entity.partner.repository;

import com.guide.run.admin.dto.response.partner.AdminPartnerResponse;
import com.guide.run.global.scheduler.dto.AttendAndPartnerDto;
import com.guide.run.partner.entity.dto.MyPagePartner;
import com.guide.run.user.entity.type.UserType;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.EnumPath;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.List;

import static com.guide.run.attendance.entity.QAttendance.attendance;
import static com.guide.run.partner.entity.partner.QPartner.partner;
import static com.guide.run.partner.entity.partner.QPartnerLike.partnerLike;
import static com.guide.run.user.entity.user.QUser.user;

public class PartnerRepositoryImpl implements PartnerRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public PartnerRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<MyPagePartner> findMyPartner(String privateId, String sort, int limit, int start, UserType userType) {
        return queryFactory
                .selectDistinct(Projections.constructor(MyPagePartner.class,
                        user.userId,
                        user.img,
                        user.role,
                        user.type,
                        user.name,
                        user.recordDegree,
                        partner.trainingIds,
                        partner.contestIds,
                        ExpressionUtils.as(
                                JPAExpressions.select(partnerLike.count())
                                        .from(partnerLike)
                                        .where(user.privateId.eq(partnerLike.recId)), "like"),
                        ExpressionUtils.as(
                                JPAExpressions.select(partnerLike.sendId)
                                        .from(partnerLike)
                                        .where(user.privateId.eq(partnerLike.recId),
                                                partnerLike.sendId.eq(privateId)), "sendId"),
                        Expressions.constant(privateId)
                ))
                .from(user)
                .leftJoin(partnerLike).on(partnerLike.recId.eq(user.privateId))
                .leftJoin(partner).on(getUserType(userType, privateId))
                .where(getPartnerId(userType), getPartnerKind("all"))
                .orderBy(partnerSortCond(sort))
                .offset(start)
                .limit(limit)
                .fetch();
    }

    @Override
    public long countMyPartner(String privateId, UserType userType) {
        Long count = queryFactory
                .select(partner.countDistinct())
                .from(user)
                .leftJoin(partnerLike).on(partnerLike.recId.eq(user.privateId))
                .leftJoin(partner).on(getUserType(userType, privateId))
                .where(getPartnerId(userType), getPartnerKind("all"))
                .fetchOne();
        return count != null ? count : 0;
    }

    @Override
    public List<AdminPartnerResponse> getAdminPartner(String privateId, UserType type, String kind, int limit, int start) {
        return queryFactory
                .selectDistinct(Projections.constructor(AdminPartnerResponse.class,
                        user.userId,
                        user.img,
                        user.role,
                        user.type,
                        user.name,
                        user.recordDegree,
                        ExpressionUtils.as(
                                JPAExpressions.select(partnerLike.count())
                                        .from(partnerLike)
                                        .where(user.privateId.eq(partnerLike.recId)), "like")
                ))
                .from(user)
                .leftJoin(partnerLike).on(partnerLike.recId.eq(user.privateId))
                .leftJoin(partner).on(getUserType(type, privateId))
                .where(getPartnerId(type), getPartnerKind(kind))
                .orderBy(partner.updatedAt.desc())
                .offset(start)
                .limit(limit)
                .fetch();
    }

    @Override
    public long countAdminPartner(String privateId, UserType type, String kind) {
        Long count = queryFactory
                .select(partner.countDistinct())
                .from(user)
                .leftJoin(partnerLike).on(partnerLike.recId.eq(user.privateId))
                .leftJoin(partner).on(getUserType(type, privateId))
                .where(getPartnerId(type), getPartnerKind(kind))
                .fetchOne();
        return count != null ? count : 0;
    }

    @Override
    public List<AdminPartnerResponse> searchAdminPartner(String privateId, UserType type, String text, int limit, int start) {
        return queryFactory
                .selectDistinct(Projections.constructor(AdminPartnerResponse.class,
                        user.userId,
                        user.img,
                        user.role,
                        user.type,
                        user.name,
                        user.recordDegree,
                        ExpressionUtils.as(
                                JPAExpressions.select(partnerLike.count())
                                        .from(partnerLike)
                                        .where(user.privateId.eq(partnerLike.recId)), "like")
                ))
                .from(user)
                .leftJoin(partnerLike).on(partnerLike.recId.eq(user.privateId))
                .leftJoin(partner).on(getUserType(type, privateId))
                .where(getPartnerId(type),
                        searchCondition(text)
                )
                .orderBy(partner.updatedAt.desc())
                .offset(start)
                .limit(limit)
                .fetch();
    }

    @Override
    public long searchAdminPartnerCount(String privateId, UserType type, String text) {
        Long count = queryFactory
                .select(partner.countDistinct())
                .from(user)
                .leftJoin(partnerLike).on(partnerLike.recId.eq(user.privateId))
                .leftJoin(partner).on(getUserType(type, privateId))
                .where(getPartnerId(type),
                        searchCondition(text)
                )
                .fetchOne();
        return count != null ? count : 0;
    }

    @Override
    public List<AttendAndPartnerDto> getEndEventAttendanceAndPartner(long eventId) {
        return queryFactory
                .selectDistinct(Projections.constructor(AttendAndPartnerDto.class,
                        user.privateId,
                        user.type,
                        partner.viId,
                        partner.guideId,
                        attendance.isAttend,
                        partner.trainingIds,
                        partner.contestIds
                ))
                .from(attendance)
                .leftJoin(user).on(user.privateId.eq(attendance.privateId))
                .leftJoin(partner).on(getUserType2(user.type, user.privateId))
                .where(
                        attendance.isAttend.eq(true),
                        attendance.eventId.eq(eventId)
                )
                .fetch();
    }


    /**
     * where 조건: 현재 사용자의 partner 데이터 필터링.
     * - GUIDE: partner.guideId = privateId
     * - VI: partner.viId = privateId
     */
    private BooleanExpression getPartnerId(UserType type) {
        return type.equals(UserType.GUIDE)
                ? partner.viId.eq(user.privateId)
                : partner.guideId.eq(user.privateId);
    }

    /**
     * getUserType: 기준으로 전달받은 privateId와 partner 테이블의 조건을 매칭
     * - GUIDE인 경우: partner.guideId = 전달받은 privateId
     * - VI인 경우: partner.viId = 전달받은 privateId
     */
    private BooleanExpression getUserType(UserType type, String privateId) {
        return type.equals(UserType.GUIDE)
                ? partner.guideId.eq(privateId)
                : partner.viId.eq(privateId);
    }

    /**
     * 검색 조건: user의 이름 또는 recordDegree에 텍스트가 포함되는지 확인.
     */
    private BooleanExpression searchCondition(String text) {
        return user.name.contains(text)
                .or(user.recordDegree.toUpperCase().contains(text.toUpperCase()));
    }

    /**
     * 정렬 조건:
     * - RECENT: partner.updatedAt 내림차순
     * - COUNT: (trainingIds 개수 + contestIds 개수) 내림차순
     */
    private OrderSpecifier<?> partnerSortCond(String sort) {
        if ("COUNT".equalsIgnoreCase(sort)) {
            return partnerEventCount().desc();
        }
        return partner.updatedAt.desc();
    }

    private NumberExpression<Integer> partnerEventCount() {
        return setSize(partner.trainingIds).add(setSize(partner.contestIds));
    }

    private NumberExpression<Integer> setSize(Expression<?> field) {
        return Expressions.numberTemplate(
                Integer.class,
                "CASE WHEN {0} IS NULL OR {0} = '' THEN 0 ELSE LENGTH({0}) - LENGTH(REPLACE({0}, ',', '')) + 1 END",
                field
        );
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

    /**
     * getEndEventAttendanceAndPartner에서 partner와 조인할 때 사용하는 조건.
     */
    private BooleanExpression getUserType2(EnumPath<UserType> type, StringPath privateId) {
        return type.eq(UserType.GUIDE).and(partner.guideId.eq(privateId))
                .or(type.eq(UserType.VI).and(partner.viId.eq(privateId)));
    }
}
