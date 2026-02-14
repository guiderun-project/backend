package com.guide.run.user.repository.withdrawal;

import com.guide.run.admin.dto.condition.WithdrawalSortCond;
import com.guide.run.admin.dto.response.user.WithdrawalItem;
import com.guide.run.user.entity.type.UserType;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.guide.run.user.entity.QWithdrawal.withdrawal;

public class WithdrawalRepositoryImpl implements WithdrawalRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public WithdrawalRepositoryImpl(EntityManager em){

        this.queryFactory = new JPAQueryFactory(em);
    }


    @Override
    public List<WithdrawalItem> sortWithdrawal(int start, int limit, WithdrawalSortCond cond) {
        List<WithdrawalItem> items = queryFactory.select(
                        Projections.constructor(
                                WithdrawalItem.class,
                                withdrawal.userId,
                                withdrawal.role,
                                withdrawal.type,
                                withdrawal.name,
                                withdrawal.recordDegree.as("team"),
                                withdrawal.gender,
                                withdrawal.deleteReasons.as("reason"),
                                formattedDate(withdrawal.updatedAt).as("update_date"),
                                formattedTime(withdrawal.updatedAt).as("update_time")
                        )

                )
                .from(withdrawal)
                .orderBy(createWithdrawalOrder(cond))
                .offset(start)
                .limit(limit)
                .fetch();

        return items;
    }

    @Override
    public long sortWithdrawalCount() {
        long count = queryFactory.select(withdrawal.count())
                .from(withdrawal)
                .fetchOne();
        return count;
    }

    @Override
    public List<WithdrawalItem> searchWithdrawal(String text, int start, int limit, WithdrawalSortCond cond) {
        List<WithdrawalItem> items = queryFactory.select(
                        Projections.constructor(
                                WithdrawalItem.class,
                                withdrawal.userId,
                                withdrawal.role,
                                withdrawal.type,
                                withdrawal.name,
                                withdrawal.recordDegree.as("team"),
                                withdrawal.gender,
                                withdrawal.deleteReasons.as("reason"),
                                formattedDate(withdrawal.updatedAt).as("update_date"),
                                formattedTime(withdrawal.updatedAt).as("update_time")
                        )

                )
                .from(withdrawal)
                .where((withdrawal.name.contains(text)
                                .or(withdrawal.recordDegree.toUpperCase().contains(text.toUpperCase()))

                                //todo : 검색 조건 추가 필요
                        ))
                .orderBy(createWithdrawalOrder(cond))
                .offset(start)
                .limit(limit)
                .fetch();

        return items;
    }

    @Override
    public long searchWithdrawalCount(String text) {

        long count = queryFactory.select(withdrawal.count())
                .from(withdrawal)
                .where(
                        (withdrawal.name.contains(text)
                                .or(withdrawal.recordDegree.toUpperCase().contains(text.toUpperCase()))

                                //todo : 검색 조건 추가 필요
                        ))
                .fetchOne();
        return count;
    }

    private OrderSpecifier<?>[] createWithdrawalOrder(WithdrawalSortCond cond){
        //정렬 순서 때문에 일부러 if 문을 여러 개 사용
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
        NumberExpression<Integer> typeOrder = new CaseBuilder()
                .when(withdrawal.type.eq(UserType.VI)).then(0)
                .when(withdrawal.type.eq(UserType.GUIDE)).then(1)
                .otherwise(2);

        if (cond.getTime()==0 || cond.getTime()==2) {
            orderSpecifiers.add(new OrderSpecifier<>(Order.DESC, withdrawal.updatedAt));
        }

        if (cond.getType()==0) {
            orderSpecifiers.add(new OrderSpecifier<>(Order.DESC, typeOrder));
        }

        if (cond.getGender()==0) {
            orderSpecifiers.add(new OrderSpecifier<>(Order.DESC, withdrawal.gender));
        }

        if (cond.getName_team()==0) {
            orderSpecifiers.add(new OrderSpecifier<>(Order.DESC, withdrawal.recordDegree));
            orderSpecifiers.add(new OrderSpecifier<>(Order.DESC, withdrawal.name));
        }

        if (cond.getType()==1) {
            orderSpecifiers.add(new OrderSpecifier<>(Order.ASC, typeOrder));
        }

        if (cond.getTime()==1) {
            orderSpecifiers.add(new OrderSpecifier<>(Order.ASC, withdrawal.updatedAt));
        }

        if (cond.getGender()==1) {
            orderSpecifiers.add(new OrderSpecifier<>(Order.ASC, withdrawal.gender));
        }

        if (cond.getName_team()==1) {
            orderSpecifiers.add(new OrderSpecifier<>(Order.ASC, withdrawal.recordDegree));
            orderSpecifiers.add(new OrderSpecifier<>(Order.ASC, withdrawal.name));
        }

        return orderSpecifiers.toArray(new OrderSpecifier[0]);
    }
    private StringExpression formattedDate(DateTimePath<LocalDateTime> localDateTime){
        return Expressions.stringTemplate("FUNCTION('DATE_FORMAT', {0}, {1})"
                , localDateTime
                , ConstantImpl.create("%Y-%m-%d"));
    }

    private StringExpression formattedTime(DateTimePath<LocalDateTime> localDateTime){
        return Expressions.stringTemplate("FUNCTION('TIME_FORMAT', {0}, {1})"
                , localDateTime
                , ConstantImpl.create("%H:%i:%s"));
    }

}
