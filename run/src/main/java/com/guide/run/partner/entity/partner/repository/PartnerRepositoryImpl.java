package com.guide.run.partner.entity.partner.repository;

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
    public List<MyPagePartner> findMyPartner(String privateId, String sort, int limit, int start, String userType) {
        List<MyPagePartner> result = queryFactory
                .select(
                        Projections.constructor(MyPagePartner.class,
                                user.userId,
                                user.img,
                                user.role,
                                user.type,
                                user.name,
                                user.recordDegree,
                                partner.trainingCnt,
                                partner.contestCnt,
                                partnerLike.sendIds,
                                constantAs(privateId, user.privateId)
                        )

                )
                .from(user)
                .join(partnerLike).on(partnerLike.recId.eq(user.privateId))
                .join(partner).on(partnerCond(userType, privateId))
                .orderBy(
                        partnerSortConde(sort)
                )
                .where(userCond(userType))
                .offset(start)
                .limit(limit)
                .fetch();
        return result;
    }

    @Override
    public int countMyPartner(String privateId, String userType) {
        int size = queryFactory
                .selectFrom(partner)
                .where(partnerCountCond(userType, privateId))
                .fetch().size();
        return size;
    }

    private BooleanExpression partnerCond(String userType, String privateId){
        if(userType.equals(UserType.GUIDE.getValue())){
            return partner.guideId.eq(privateId);
        }else{
            return partner.viId.eq(privateId);
        }
    }

    private BooleanExpression userCond(String userType){
        if(userType.equals(UserType.GUIDE.getValue())){
            return user.userId.eq(partner.viId);
        }else{
            return user.userId.eq(partner.guideId);
        }
    }

   private OrderSpecifier partnerSortConde(String sort){
        if(sort.equals("RECENT")){ //최근순
            return partner.createdAt.desc();
        }else if(sort.equals("COUNT")){ //많이 뛴 순
            return partner.contestCnt.add(partner.trainingCnt).desc();
        }
        return null;
   }

   private BooleanExpression partnerCountCond(String userType, String privateId){
       if(userType.equals(UserType.GUIDE.getValue())){
           return partner.guideId.eq(privateId);
       }else{
           return partner.viId.eq(privateId);
       }
   }
}
