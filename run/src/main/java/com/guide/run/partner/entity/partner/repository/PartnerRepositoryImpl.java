package com.guide.run.partner.entity.partner.repository;

import com.guide.run.admin.dto.response.partner.AdminPartnerResponse;
import com.guide.run.partner.entity.dto.MyPagePartner;
import com.guide.run.user.entity.type.UserType;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.List;

import static com.guide.run.partner.entity.partner.QPartner.partner;
import static com.guide.run.partner.entity.partner.QPartnerLike.partnerLike;
import static com.guide.run.user.entity.user.QUser.user;
import static com.querydsl.core.types.dsl.Expressions.constantAs;

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
                                partner.trainingIds.size().as("trainingCnt"),
                                partner.contestIds.size().as("contestCnt"),
                                partnerLike.sendIds.size(),
                                constantAs(privateId, user.privateId)
                        )

                )
                .from(user)
                .join(partnerLike).on(partnerLike.recId.eq(user.privateId))
                .join(partner).on(getUserType(userType, privateId))
                .orderBy(
                        partnerSortCond(sort)
                )
                .where()
                .offset(start)
                .limit(limit)
                .fetch();
        return result;
    }

    @Override
    public int countMyPartner(String privateId, UserType userType) {
        int size = queryFactory
                .selectFrom(partner)
                .where(getUserType(userType, privateId))
                .fetch().size();
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
                        partnerLike.sendIds.size().as("like")
                ))
                .from(user, partnerLike, partner)
                .where(
                        getUserType(type, privateId),
                        getPartnerLike(type),
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
                .select(user.userId)
                .from(user, partnerLike, partner)
                .where(
                        getUserType(type, privateId),
                        getPartnerLike(type),
                        getPartnerKind(kind)
                )
                .fetch().size();
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
                        partnerLike.sendIds.size().as("like")
                ))
                .from(user, partnerLike, partner)
                .where(
                        getUserType(type, privateId),
                        getPartnerLike(type),
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
                .select(user.userId)
                .from(user, partnerLike, partner)
                .where(
                        getUserType(type, privateId),
                        getPartnerLike(type),
                        (user.name.contains(text)
                                .or(user.recordDegree.toUpperCase().contains(text.toUpperCase()))
                                //todo : 검색 조건 추가 필요
                        )
                )
                .fetch().size();
        return count;
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

    private BooleanExpression getPartnerLike(UserType type){
        if(type.equals(UserType.GUIDE)){
            return partnerLike.recId.eq(partner.viId);
        } else if (type.equals(UserType.VI)) {
            return partnerLike.recId.eq(partner.guideId);
        }else{
            return null;
        }
    }


    private OrderSpecifier partnerSortCond(String sort){
        if(sort.equals("RECENT")){ //최근순
            return partner.createdAt.desc();
        }else if(sort.equals("COUNT")){ //많이 뛴 순
            return partner.contestIds.size().add(partner.trainingIds.size()).desc();
        }
        return null;
    }

    private BooleanExpression getPartnerKind(String kind){
        if(kind.equals("COMPETITON")){
        return partner.contestIds.isNotEmpty();
        } else if (kind.equals("TRAINING")) {
            return partner.trainingIds.isNotEmpty();
        }else{
            return null;
        }
    }


}
