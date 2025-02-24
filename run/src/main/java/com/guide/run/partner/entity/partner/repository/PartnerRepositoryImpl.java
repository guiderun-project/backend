package com.guide.run.partner.entity.partner.repository;

import com.guide.run.admin.dto.response.partner.AdminPartnerResponse;
import com.guide.run.attendance.entity.QAttendance;
import com.guide.run.global.scheduler.dto.AttendAndPartnerDto;
import com.guide.run.partner.entity.dto.MyPagePartner;
import com.guide.run.user.entity.type.UserType;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.List;

import static com.guide.run.attendance.entity.QAttendance.attendance;
import static com.guide.run.partner.entity.partner.QPartner.partner;
import static com.guide.run.partner.entity.partner.QPartnerLike.partnerLike;
import static com.guide.run.user.entity.user.QUser.user;

public class PartnerRepositoryImpl implements PartnerRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    public PartnerRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<MyPagePartner> findMyPartner(String privateId, String sort, int limit, int start, UserType userType) {
        List<MyPagePartner> result = queryFactory
                .select(
                        Projections.constructor(MyPagePartner.class,
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
                                                .where(user.privateId.eq(partnerLike.recId)),"like" ),
                                ExpressionUtils.as(
                                        JPAExpressions.select(partnerLike.sendId)
                                                .from(partnerLike)
                                                .where(user.privateId.eq(partnerLike.recId),
                                                        partnerLike.sendId.eq(privateId)),"sendId"),
                                Expressions.constant(privateId)
                        )
                )
                .from(user)
                .leftJoin(partnerLike).on(partnerLike.recId.eq(user.privateId))
                .leftJoin(partner).on(getUserType(userType, privateId))
                .orderBy(
                        partnerSortCond(sort)
                )
                .where(getPartnerId(userType))
                .offset(start)
                .limit(limit)
                .fetch();
        return result;
    }

    @Override
    public long countMyPartner(String privateId, UserType userType) {
        long size = queryFactory
                .select(partner.count())
                .from(partner)
                .where(getUserType(userType, privateId))
                .fetchOne();
        return size;
    }


    @Override
    public List<AdminPartnerResponse> getAdminPartner(String privateId, UserType type, String kind, int limit, int start) {
        List<AdminPartnerResponse> responses = queryFactory
                .select(Projections.constructor(
                        AdminPartnerResponse.class,
                        user.userId,
                        user.img,
                        user.role,
                        user.type,
                        user.name,
                        user.recordDegree,
                        ExpressionUtils.as(
                                JPAExpressions.select(partnerLike.count())
                                        .from(partnerLike)
                                        .where(user.privateId.eq(partnerLike.recId)),"like" )
                ))
                .from(user)
                .leftJoin(partnerLike).on(partnerLike.recId.eq(user.privateId))
                .leftJoin(partner).on(getUserType(type,privateId))
                .where(
                        getPartnerId(type),
                        getPartnerKind(kind)
                )
                .offset(start)
                .limit(limit)
                .orderBy(partner.updatedAt.desc())
                .fetch();
        return responses;
    }

    @Override
    public long countAdminPartner(String privateId, UserType type, String kind) {
        long response = queryFactory
                .select(partner.count())
                .from(partner)
                .where(
                        getUserType(type, privateId),
                        getPartnerKind(kind)
                )
                .fetchOne();
        return response;
    }

    @Override
    public List<AdminPartnerResponse> searchAdminPartner(String privateId, UserType type, String text, int limit, int start) {
        List<AdminPartnerResponse> responses = queryFactory
                .select(Projections.constructor(
                        AdminPartnerResponse.class,
                        user.userId,
                        user.img,
                        user.role,
                        user.type,
                        user.name,
                        user.recordDegree,
                        ExpressionUtils.as(
                                JPAExpressions.select(partnerLike.count())
                                        .from(partnerLike)
                                        .where(user.privateId.eq(partnerLike.recId)),"like" )
                ))
                .from(user)
                .leftJoin(partnerLike).on(partnerLike.recId.eq(user.privateId))
                .leftJoin(partner).on(getUserType(type,privateId))
                .where(
                        getPartnerId(type),
                        (user.name.contains(text)
                                .or(user.recordDegree.toUpperCase().contains(text.toUpperCase()))
                                //todo : 검색 조건 추가 필요
                                )
                )
                .offset(start)
                .limit(limit)
                .orderBy(partner.updatedAt.desc())
                .fetch();
        return responses;
    }

    @Override
    public long searchAdminPartnerCount(String privateId, UserType type, String text) {
        long count = queryFactory
                .select(partner.count())
                .from(partner)
                .where(
                        getUserType(type, privateId),
                        (user.name.contains(text)
                                .or(user.recordDegree.toUpperCase().contains(text.toUpperCase()))
                                //todo : 검색 조건 추가 필요
                        )
                )
                .fetchOne();
        return count;
    }

    @Override
    public List<AttendAndPartnerDto> getEndEventAttendanceAndPartner(long eventId) {
        List<AttendAndPartnerDto> list = queryFactory.select(
                        Projections.constructor(AttendAndPartnerDto.class,
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
                .where(attendance.isAttend.eq(true),
                        attendance.eventId.eq(eventId))
                .fetch();
        return list;
    }

    private BooleanExpression getPartnerId(UserType type){
        if(type.equals(UserType.GUIDE)){
            return partner.viId.eq(user.privateId);
        }else if(type.equals(UserType.VI)){
            return partner.guideId.eq(user.privateId);
        }else{
            return null;
        }
    }

    private BooleanExpression getUserType(UserType type, String privateId){
        if(type.equals(UserType.GUIDE)){
            return partner.guideId.eq(privateId);
        } else if (type.equals(UserType.VI)) {
            return partner.viId.eq(privateId);
        }else{
            return null;
        }
    }

    private BooleanExpression getUserType2(EnumPath<UserType> type, StringPath privateId){
        if(type.equals(UserType.GUIDE)){
            return partner.guideId.eq(privateId);
        } else {
            return partner.viId.eq(privateId);
        }
    }

    private OrderSpecifier partnerSortCond(String sort){
        if(sort.equals("RECENT")){ //최근순
            return partner.updatedAt.desc();
        }else if(sort.equals("COUNT")){ //많이 뛴 순
            return partner.updatedAt.desc();
        }
        return partner.updatedAt.desc();
    }

    private BooleanExpression getPartnerKind(String kind){
        if(kind.equals("COMPETITON")){
            return Expressions.booleanTemplate("COALESCE(LENGTH({0}), 0) > 0", partner.contestIds);
        } else if (kind.equals("TRAINING")) {
            return Expressions.booleanTemplate("COALESCE(LENGTH({0}), 0) > 0", partner.trainingIds);
        } else {
            return null;
        }
    }

}
