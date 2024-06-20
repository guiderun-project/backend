package com.guide.run.event.entity.repository;

import com.guide.run.admin.dto.condition.EventApplyCond;
import com.guide.run.admin.dto.response.event.AdminEventApplyItem;
import com.guide.run.user.entity.QWithdrawal;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.ArrayList;
import java.util.List;

import static com.guide.run.event.entity.QEventForm.eventForm;
import static com.guide.run.user.entity.QWithdrawal.withdrawal;

public class EventFormRepositoryAdminImpl implements EventFormRepositoryAdmin{
    private final JPAQueryFactory queryFactory;

    public EventFormRepositoryAdminImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<AdminEventApplyItem> getEventApplyList(long eventId, EventApplyCond cond, int start, int limit) {
        List<AdminEventApplyItem> list = queryFactory.select(Projections.constructor(AdminEventApplyItem.class,
                        withdrawal.userId,
                        withdrawal.role,
                        withdrawal.type,
                        withdrawal.name,
                        withdrawal.recordDegree,
                        eventForm.createdAt
                ))
                .from(eventForm, withdrawal)
                .where(eventForm.privateId.eq(withdrawal.privateId),
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
                .from(eventForm, withdrawal)
                .where(eventForm.privateId.eq(withdrawal.privateId),
                        eventForm.eventId.eq(eventId))
                .fetchOne();
        return size;
    }

    private OrderSpecifier[] createOrderSpec(EventApplyCond cond){
        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();

        if(cond.getTime()==0 || cond.getTime()==2){
            orderSpecifiers.add(new OrderSpecifier<>(Order.DESC, eventForm.createdAt));
        }

        if(cond.getTeam()==0){
            orderSpecifiers.add(new OrderSpecifier(Order.DESC, withdrawal.recordDegree));
        }

        if(cond.getType_name()==0){
            orderSpecifiers.add(new OrderSpecifier<>(Order.DESC, withdrawal.name));
            orderSpecifiers.add(new OrderSpecifier(Order.DESC, withdrawal.type));
        }

        if(cond.getTime()==1){
            orderSpecifiers.add(new OrderSpecifier<>(Order.ASC, eventForm.createdAt));
        }

        if(cond.getTeam()==1){
            orderSpecifiers.add(new OrderSpecifier(Order.ASC, withdrawal.recordDegree));
        }

        if(cond.getType_name()==1){
            orderSpecifiers.add(new OrderSpecifier<>(Order.ASC, withdrawal.name));
            orderSpecifiers.add(new OrderSpecifier(Order.ASC, withdrawal.type));
        }

        return orderSpecifiers.toArray(new OrderSpecifier[orderSpecifiers.size()]);

    }
}
