package com.guide.run.user.repository.user;

import com.guide.run.admin.dto.condition.UserSortCond;
import com.guide.run.admin.dto.response.NewUserResponse;
import com.guide.run.admin.dto.response.UserItem;
import com.guide.run.user.entity.type.Role;
import com.guide.run.user.entity.type.UserType;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.ArrayList;
import java.util.List;

import static com.guide.run.partner.entity.partner.QPartnerLike.partnerLike;
import static com.guide.run.user.entity.user.QUser.user;

public class UserRepositoryAdminImpl implements UserRepositoryAdmin{

    private final JPAQueryFactory queryFactory;

    public UserRepositoryAdminImpl(EntityManager em){
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public long sortAdminUserCount() {

        long count = queryFactory
                .select(user.userId).from(user)
                .where(
                        user.role.ne(Role.ROLE_DELETE),
                        user.role.ne(Role.ROLE_NEW)
                )
                .fetch().size();

        return count;

    }

    @Override
    public List<UserItem> sortAdminUser(int start, int limit, UserSortCond cond) {
        StringExpression formattedDate = Expressions.stringTemplate("FUNCTION('DATE_FORMAT', {0}, {1})"
                , user.updatedAt
                , ConstantImpl.create("%Y-%m-%d"));
        StringExpression formattedTime = Expressions.stringTemplate("FUNCTION('TIME_FORMAT', {0}, {1})"
                , user.updatedAt
                , ConstantImpl.create("%H:%i:%s"));

        List<UserItem> userItems = queryFactory
                .select(
                        Projections.constructor(
                                UserItem.class,
                                user.userId,
                                user.img,
                                user.role,
                                user.age,
                                user.type,
                                user.name,
                                user.recordDegree.as("team"),
                                user.gender,
                                user.snsId,
                                user.phoneNumber,
                                user.trainingCnt.add(user.competitionCnt).as("totalCnt"),
                                user.trainingCnt,
                                user.competitionCnt,
                                formattedDate.as("update_date"),
                                formattedTime.as("update_time")

                        )

                ).from(user)
                .where(
                        user.role.ne(Role.ROLE_DELETE),
                        user.role.ne(Role.ROLE_NEW)
                )
                .orderBy(
                    createOrderSpec(cond)
                )
                .limit(limit)
                .offset(start)
                .fetch();

        return userItems;

    }

    private OrderSpecifier[] createOrderSpec(UserSortCond cond) {
        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();

        //null은 기본값. true false가 더 우선순위를 가지도록 하는 자료구조를 사용해야 한다.
        //어레이 리스트는 add가 들어가면 앞에 값이 추가됨. null일 경우를 뒤로 밀도록 해야 함.
        //먼저 들어가는 걸 우선순위로 넣도록 해야 하는데.....

        NumberExpression<Integer> roleOrder = new CaseBuilder()
                .when(user.role.eq(Role.ROLE_ADMIN)).then(0)
                .when(user.role.eq(Role.ROLE_COACH)).then(1)
                .when(user.role.eq(Role.ROLE_USER)).then(2)
                .when(user.role.eq(Role.ROLE_WAIT)).then(3)
                .otherwise(4);

        NumberExpression<Integer> typeOrder = new CaseBuilder()
                .when(user.type.eq(UserType.VI)).then(0)
                .when(user.type.eq(UserType.GUIDE)).then(1)
                .otherwise(2);


        if (cond.isApproval()) {
            orderSpecifiers.add(new OrderSpecifier(Order.ASC, roleOrder));
        }

        if (cond.isType()) {
            orderSpecifiers.add(new OrderSpecifier(Order.ASC, typeOrder));
        }

        if (cond.isTime()) {
            orderSpecifiers.add(new OrderSpecifier(Order.ASC, user.updatedAt));
        }

        if (cond.isGender()) {
            orderSpecifiers.add(new OrderSpecifier(Order.ASC, user.gender));
        }

        if (cond.isName_team()) {
            orderSpecifiers.add(new OrderSpecifier(Order.ASC, user.recordDegree));
            orderSpecifiers.add(new OrderSpecifier(Order.ASC, user.name));
        }


        if (!cond.isApproval()) {
            orderSpecifiers.add(new OrderSpecifier(Order.DESC, roleOrder));
        }

        if (!cond.isType()) {
            orderSpecifiers.add(new OrderSpecifier(Order.DESC, typeOrder));
        }

        if (!cond.isTime()) {
            orderSpecifiers.add(new OrderSpecifier(Order.DESC, user.updatedAt));
        }

        if (!cond.isGender()) {
            orderSpecifiers.add(new OrderSpecifier(Order.DESC, user.gender));
        }

        if (!cond.isName_team()) {
            orderSpecifiers.add(new OrderSpecifier(Order.DESC, user.recordDegree));
            orderSpecifiers.add(new OrderSpecifier(Order.DESC, user.name));
        }

        return orderSpecifiers.toArray(new OrderSpecifier[orderSpecifiers.size()]);
    }

    @Override
    public List<UserItem> searchAdminUser(int start, int limit, UserSortCond cond, String text) {
        StringExpression formattedDate = Expressions.stringTemplate("FUNCTION('DATE_FORMAT', {0}, {1})"
                , user.updatedAt
                , ConstantImpl.create("%Y-%m-%d"));
        StringExpression formattedTime = Expressions.stringTemplate("FUNCTION('TIME_FORMAT', {0}, {1})"
                , user.updatedAt
                , ConstantImpl.create("%H:%i:%s"));

        List<UserItem> userItems = queryFactory
                .select(
                        Projections.constructor(
                                UserItem.class,
                                user.userId,
                                user.img,
                                user.role,
                                user.age,
                                user.type,
                                user.name,
                                user.recordDegree.as("team"),
                                user.gender,
                                user.snsId,
                                user.phoneNumber,
                                user.trainingCnt.add(user.competitionCnt).as("totalCnt"),
                                user.trainingCnt,
                                user.competitionCnt,
                                formattedDate.as("update_date"),
                                formattedTime.as("update_time")

                        )

                ).from(user)
                .where(
                        //이름, 기록, sns, 나이, 전화번호
                        (user.name.contains(text)
                                .or(user.recordDegree.contains(text.toUpperCase()))
                                .or(user.snsId.toUpperCase().contains(text.toUpperCase()))
                                .or(user.phoneNumber.contains(text))),
                        user.role.ne(Role.ROLE_DELETE),
                        user.role.ne(Role.ROLE_NEW)


                )
                .orderBy(
                        createOrderSpec(cond)
                )
                .limit(limit)
                .offset(start)
                .fetch();

        return userItems;

    }

    @Override
    public long searchAdminUserCount(String text) {

        long count = queryFactory
                .select(user.userId)
                .from(user)
                .where(
                        //이름, 기록, sns, 나이, 전화번호
                        (user.name.contains(text)
                                .or(user.recordDegree.contains(text.toUpperCase()))
                                .or(user.snsId.toUpperCase().contains(text.toUpperCase()))
                                .or(user.phoneNumber.contains(text))),
                        user.role.ne(Role.ROLE_DELETE),
                        user.role.ne(Role.ROLE_NEW)
                ).fetch().size();
        return count;
    }

    @Override
    public List<NewUserResponse> findNewUser(int start, int limit) {
        List<NewUserResponse> result = queryFactory.select(
                        Projections.constructor(NewUserResponse.class,
                                user.userId,
                                user.img,
                                user.role,
                                user.type,
                                user.name,
                                user.trainingCnt,
                                user.competitionCnt.as("contestCnt"),
                                partnerLike.sendIds.size().as("like")
                        )
                ).from(user, partnerLike)
                .where(user.privateId.eq(partnerLike.recId),
                        user.role.ne(Role.ROLE_DELETE),
                        user.role.ne(Role.ROLE_NEW))
                .orderBy(user.createdAt.desc())
                .fetch();
        return result;
    }
}
