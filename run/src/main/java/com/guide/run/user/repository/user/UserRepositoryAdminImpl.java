package com.guide.run.user.repository.user;

import com.guide.run.admin.dto.condition.UserSortCond;
import com.guide.run.admin.dto.condition.WithdrawalSortCond;
import com.guide.run.admin.dto.response.user.NewUserResponse;
import com.guide.run.admin.dto.response.user.UserItem;
import com.guide.run.admin.dto.response.user.WithdrawalItem;
import com.guide.run.user.entity.QArchiveData;
import com.guide.run.user.entity.type.Role;
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

import static com.guide.run.partner.entity.partner.QPartnerLike.partnerLike;
import static com.guide.run.user.entity.QArchiveData.archiveData;
import static com.guide.run.user.entity.user.QUser.user;

public class UserRepositoryAdminImpl implements UserRepositoryAdmin{

    private final JPAQueryFactory queryFactory;

    public UserRepositoryAdminImpl(EntityManager em){
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public long sortAdminUserCount() {

        long count = queryFactory
                .select(user.count()).from(user)
                .where(
                        user.role.ne(Role.ROLE_DELETE),
                        user.role.ne(Role.ROLE_NEW)
                )
                .fetchOne();

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
                .select(user.count())
                .from(user)
                .where(
                        //이름, 기록, sns, 나이, 전화번호
                        (user.name.contains(text)
                                .or(user.recordDegree.contains(text.toUpperCase()))
                                .or(user.snsId.toUpperCase().contains(text.toUpperCase()))
                                .or(user.phoneNumber.contains(text))),
                        user.role.ne(Role.ROLE_DELETE),
                        user.role.ne(Role.ROLE_NEW)
                ).fetchOne();
        return count;
    }

    @Override
    public List<NewUserResponse> findNewUser(int start, int limit, String privateId) {
        List<NewUserResponse> result = queryFactory.select(
                        Projections.constructor(NewUserResponse.class,
                                user.userId,
                                user.img,
                                user.role,
                                user.type,
                                user.name,
                                user.trainingCnt,
                                user.competitionCnt.as("contestCnt"),
                                partnerLike.sendIds.as("like"),
                                Expressions.constant(privateId)
                        )
                ).from(user)
                .leftJoin(partnerLike).on(user.privateId.eq(partnerLike.recId))
                .where(user.role.ne(Role.ROLE_DELETE),
                        user.role.ne(Role.ROLE_NEW))
                .offset(start)
                .limit(limit)
                .orderBy(user.createdAt.desc())
                .fetch();
        return result;
    }

    @Override
    public List<WithdrawalItem> sortWithdrawal(int start, int limit, WithdrawalSortCond cond) {
        List<WithdrawalItem> items = queryFactory.select(
                        Projections.constructor(
                                WithdrawalItem.class,
                                user.userId,
                                user.role,
                                user.type,
                                user.name,
                                user.recordDegree.as("team"),
                                user.gender,
                                archiveData.deleteReasons.as("reason"),
                                formattedDate(user.updatedAt).as("update_date"),
                                formattedTime(user.updatedAt).as("update_time")
                        )

                )
                .from(user, archiveData)
                .where(user.role.eq(Role.ROLE_DELETE),
                        user.privateId.eq(archiveData.privateId))
                .orderBy(createWithdrawalOrder(cond))
                .offset(start)
                .limit(limit)
                .fetch();

        return items;
    }

    @Override
    public long sortWithdrawalCount() {
        long count = queryFactory.select(user.count())
                .from(user, archiveData)
                .where(user.role.eq(Role.ROLE_DELETE),
                        user.privateId.eq(archiveData.privateId))
                .fetchOne();
        return count;
    }

    @Override
    public List<WithdrawalItem> searchWithdrawal(String text, int start, int limit, WithdrawalSortCond cond) {
        List<WithdrawalItem> items = queryFactory.select(
                        Projections.constructor(
                                WithdrawalItem.class,
                                user.userId,
                                user.role,
                                user.type,
                                user.name,
                                user.recordDegree.as("team"),
                                user.gender,
                                archiveData.deleteReasons.as("reason"),
                                formattedDate(user.updatedAt).as("update_date"),
                                formattedTime(user.updatedAt).as("update_time")
                        )

                )
                .from(user, archiveData)
                .where(user.role.eq(Role.ROLE_DELETE),
                        user.privateId.eq(archiveData.privateId),
                        (user.name.contains(text)
                                .or(user.recordDegree.toUpperCase().contains(text.toUpperCase()))

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

        long count = queryFactory.select(user.count())
                .from(user, archiveData)
                .where(user.role.eq(Role.ROLE_DELETE),
                        user.privateId.eq(archiveData.privateId),
                        (user.name.contains(text)
                                .or(user.recordDegree.toUpperCase().contains(text.toUpperCase()))

                                //todo : 검색 조건 추가 필요
                        ))
                .fetchOne();
        return count;
    }

    private OrderSpecifier[] createWithdrawalOrder(WithdrawalSortCond cond){
        //정렬 순서 때문에 일부러 if 문을 여러 개 사용
        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();
        NumberExpression<Integer> typeOrder = new CaseBuilder()
                .when(user.type.eq(UserType.VI)).then(0)
                .when(user.type.eq(UserType.GUIDE)).then(1)
                .otherwise(2);

        if (cond.getTime()==0 || cond.getTime()==2) {
            orderSpecifiers.add(new OrderSpecifier(Order.DESC, user.updatedAt));
        }

        if (cond.getType()==0) {
            orderSpecifiers.add(new OrderSpecifier(Order.DESC, typeOrder));
        }

        if (cond.getGender()==0) {
            orderSpecifiers.add(new OrderSpecifier(Order.DESC, user.gender));
        }

        if (cond.getName_team()==0) {
            orderSpecifiers.add(new OrderSpecifier(Order.DESC, user.recordDegree));
            orderSpecifiers.add(new OrderSpecifier(Order.DESC, user.name));
        }

        if (cond.getType()==1) {
            orderSpecifiers.add(new OrderSpecifier(Order.ASC, typeOrder));
        }

        if (cond.getTime()==1) {
            orderSpecifiers.add(new OrderSpecifier(Order.ASC, user.updatedAt));
        }

        if (cond.getGender()==1) {
            orderSpecifiers.add(new OrderSpecifier(Order.ASC, user.gender));
        }

        if (cond.getName_team()==1) {
            orderSpecifiers.add(new OrderSpecifier(Order.ASC, user.recordDegree));
            orderSpecifiers.add(new OrderSpecifier(Order.ASC, user.name));
        }

        return orderSpecifiers.toArray(new OrderSpecifier[orderSpecifiers.size()]);
    }


    private OrderSpecifier[] createOrderSpec(UserSortCond cond) {
        //정렬 순서 때문에 일부러 if 문을 여러 개 사용
        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();

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


        if (cond.getTime()==0 || cond.getTime()==2) {
            orderSpecifiers.add(new OrderSpecifier(Order.DESC, user.updatedAt));
        }

        if (cond.getType()==0) {
            orderSpecifiers.add(new OrderSpecifier(Order.DESC, typeOrder));
        }

        if (cond.getGender()==0) {
            orderSpecifiers.add(new OrderSpecifier(Order.DESC, user.gender));
        }

        if (cond.getName_team()==0) {
            orderSpecifiers.add(new OrderSpecifier(Order.DESC, user.recordDegree));
            orderSpecifiers.add(new OrderSpecifier(Order.DESC, user.name));
        }

        if(cond.getApproval()==0){
            orderSpecifiers.add(new OrderSpecifier<>(Order.DESC, roleOrder));
        }

        if (cond.getType()==1) {
            orderSpecifiers.add(new OrderSpecifier(Order.ASC, typeOrder));
        }

        if (cond.getTime()==1) {
            orderSpecifiers.add(new OrderSpecifier(Order.ASC, user.updatedAt));
        }

        if (cond.getGender()==1) {
            orderSpecifiers.add(new OrderSpecifier(Order.ASC, user.gender));
        }

        if (cond.getName_team()==1) {
            orderSpecifiers.add(new OrderSpecifier(Order.ASC, user.recordDegree));
            orderSpecifiers.add(new OrderSpecifier(Order.ASC, user.name));
        }
        if(cond.getApproval()==1){
            orderSpecifiers.add(new OrderSpecifier<>(Order.ASC, roleOrder));
        }

        return orderSpecifiers.toArray(new OrderSpecifier[orderSpecifiers.size()]);
    }

    private StringExpression formattedDate(DateTimePath<LocalDateTime> localDateTime){
        return Expressions.stringTemplate("FUNCTION('DATE_FORMAT', {0}, {1})"
                , localDateTime
                , ConstantImpl.create("%Y-%m-%d"));
    }

    private StringExpression formattedDateTime(DateTimePath<LocalDateTime> localDateTime){
        return Expressions.stringTemplate(
                "FUNCTION('DATE_FORMAT', {0}, {1})",
                localDateTime,
                ConstantImpl.create("%Y-%m-%d %H:%i:%s")
        );
    }

    private StringExpression formattedTime(DateTimePath<LocalDateTime> localDateTime){
        return Expressions.stringTemplate("FUNCTION('TIME_FORMAT', {0}, {1})"
                , localDateTime
                , ConstantImpl.create("%H:%i:%s"));
    }
}
