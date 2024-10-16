package com.guide.run.event.entity.repository;

import com.guide.run.admin.dto.condition.EventApplyCond;
import com.guide.run.admin.dto.response.event.AdminEventApplyItem;
import com.guide.run.user.entity.QWithdrawal;
import com.guide.run.user.entity.type.Role;
import com.guide.run.user.entity.type.UserType;
import com.guide.run.user.entity.user.QUser;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.ArrayList;
import java.util.List;

import static com.guide.run.event.entity.QEventForm.eventForm;
import static com.guide.run.user.entity.user.QUser.user;

public class EventFormRepositoryAdminImpl implements EventFormRepositoryAdmin{
    private final JPAQueryFactory queryFactory;

    public EventFormRepositoryAdminImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<AdminEventApplyItem> getEventApplyList(long eventId, EventApplyCond cond, int start, int limit) {
        List<AdminEventApplyItem> list = queryFactory.select(Projections.constructor(AdminEventApplyItem.class,
                        user.userId,
                        user.role,
                        user.type,
                        user.name,
                        eventForm.hopeTeam,
                        eventForm.createdAt,
                        user.recordDegree
                ))
                .from(eventForm, user)
                .where(eventForm.privateId.eq(user.privateId),
                        eventForm.eventId.eq(eventId))
                .orderBy(createOrderSpec(cond))
                .offset(start)
                .limit(limit)
                .fetch();
        return list;
    }

    @Override
    public long getEventApplyCount(long eventId) {
        long size = queryFactory.select(eventForm.count())
                .from(eventForm, user)
                .where(eventForm.privateId.eq(user.privateId),
                        eventForm.eventId.eq(eventId))
                .fetchOne();
        return size;
    }

    private OrderSpecifier[] createOrderSpec(EventApplyCond cond){
        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();


        NumberExpression<Integer> typeOrder = new CaseBuilder()
                .when(user.type.eq(UserType.VI)).then(0)
                .when(user.type.eq(UserType.GUIDE)).then(1)
                .otherwise(2);

        if(cond.getTime()==0){
            orderSpecifiers.add(new OrderSpecifier<>(Order.DESC, eventForm.createdAt));
        }

        if(cond.getTeam()==0){
            orderSpecifiers.add(new OrderSpecifier(Order.DESC, user.recordDegree));
        }

        if(cond.getType_name()==0){
            orderSpecifiers.add(new OrderSpecifier(Order.DESC, typeOrder));
        }

        if(cond.getTime()==1){
            orderSpecifiers.add(new OrderSpecifier<>(Order.ASC, eventForm.createdAt));
        }

        if(cond.getTeam()==1){
            orderSpecifiers.add(new OrderSpecifier(Order.ASC, user.recordDegree));
        }

        if(cond.getType_name()==1){
            orderSpecifiers.add(new OrderSpecifier(Order.ASC, typeOrder));
        }

        if(cond.getTeam()==2 && cond.getType_name()==2 && cond.getTime()==2){
            orderSpecifiers.add(new OrderSpecifier<>(Order.DESC, eventForm.createdAt));
        }

        return orderSpecifiers.toArray(new OrderSpecifier[orderSpecifiers.size()]);

    }
}
