package com.guide.run.partner.entity.partner.repository;

import com.guide.run.admin.dto.response.partner.AdminPartnerResponse;
import com.guide.run.global.scheduler.dto.AttendAndPartnerDto;
import com.guide.run.partner.entity.dto.MyPagePartner;
import com.guide.run.user.entity.type.UserType;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.EnumPath;
import com.querydsl.core.types.dsl.Expressions;
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
                .from(partner)
                .join(user).on(partnerUserJoin(userType))
                .where(
                        partnerUserWhere(userType, privateId),
                        getPartnerKind("all")
                )
                .orderBy(partnerSortCond(sort))
                .offset(start)
                .limit(limit)
                .fetch();
    }

    @Override
    public long countMyPartner(String privateId, UserType userType) {
        Long count = queryFactory
                .selectDistinct(partner.count())
                .from(partner)
                .where(
                        partnerUserWhere(userType, privateId),
                        getPartnerKind("all")
                )
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
                .from(partner)
                .join(user).on(partnerUserJoin(type))
                .where(
                        partnerUserWhere(type, privateId),
                        getPartnerKind(kind)
                )
                .offset(start)
                .limit(limit)
                .orderBy(partner.updatedAt.desc())
                .fetch();
    }

    @Override
    public long countAdminPartner(String privateId, UserType type, String kind) {
        Long count = queryFactory
                .selectDistinct(partner.count())
                .from(partner)
                .where(
                        partnerUserWhere(type, privateId),
                        getPartnerKind(kind)
                )
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
                .from(partner)
                .join(user).on(partnerUserJoin(type))
                .where(
                        partnerUserWhere(type, privateId),
                        searchCondition(text)
                )
                .offset(start)
                .limit(limit)
                .orderBy(partner.updatedAt.desc())
                .fetch();
    }

    @Override
    public long searchAdminPartnerCount(String privateId, UserType type, String text) {
        Long count = queryFactory
                .selectDistinct(partner.count())
                .from(partner)
                .join(user).on(partnerUserJoin(type))
                .where(
                        partnerUserWhere(type, privateId),
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

    // ── HELPER METHODS ──

    /**
     * 조인 조건: 사용자 타입에 따라 partner와 user의 연결 조건을 결정.
     * - GUIDE: partner.viId = user.privateId
     * - VI: partner.guideId = user.privateId
     */
    private BooleanExpression partnerUserJoin(UserType type) {
        return user.privateId.eq(type.equals(UserType.GUIDE) ? partner.viId : partner.guideId);
    }

    /**
     * where 조건: 현재 사용자의 partner 데이터 필터링.
     * - GUIDE: partner.guideId = privateId
     * - VI: partner.viId = privateId
     */
    private BooleanExpression partnerUserWhere(UserType type, String privateId) {
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
     * 정렬 조건: 현재는 RECENT와 COUNT 모두 partner.updatedAt의 내림차순 정렬.
     */
    private OrderSpecifier<?> partnerSortCond(String sort) {
        return partner.updatedAt.desc();
    }

    /**
     * 파트너의 종류 조건:
     * - "COMPETITON": contestIds에 값이 있는 경우
     * - "TRAINING": trainingIds에 값이 있는 경우
     * - 그 외("all" 또는 null): 두 컬럼 중 하나라도 값이 있으면.
     */
    private BooleanExpression getPartnerKind(String kind) {
        if ("COMPETITON".equals(kind)) {
            return Expressions.booleanTemplate("COALESCE(LENGTH({0}), 0) > 0", partner.contestIds);
        } else if ("TRAINING".equals(kind)) {
            return Expressions.booleanTemplate("COALESCE(LENGTH({0}), 0) > 0", partner.trainingIds);
        } else {
            return Expressions.booleanTemplate(
                    "COALESCE(LENGTH({0}), 0) > 0 or COALESCE(LENGTH({1}), 0) > 0",
                    partner.trainingIds, partner.contestIds
            );
        }
    }

    /**
     * getEndEventAttendanceAndPartner에서 partner와 조인할 때 사용하는 조건.
     */
    private BooleanExpression getUserType2(EnumPath<UserType> type, StringPath privateId) {
        return type.equals(UserType.GUIDE)
                ? partner.guideId.eq(privateId)
                : partner.viId.eq(privateId);
    }
}
