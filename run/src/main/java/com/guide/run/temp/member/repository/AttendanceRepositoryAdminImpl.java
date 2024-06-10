package com.guide.run.temp.member.repository;

import com.guide.run.admin.dto.response.event.AbsentDto;
import com.guide.run.user.entity.type.UserType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import static com.guide.run.temp.member.entity.QAttendance.attendance;
import static com.guide.run.user.entity.user.QUser.user;

public class AttendanceRepositoryAdminImpl implements AttendanceRepositoryAdmin{

    private final JPAQueryFactory queryFactory;

    public AttendanceRepositoryAdminImpl(EntityManager em){
        this.queryFactory = new JPAQueryFactory(em);
    }


    @Override
    public AbsentDto getAbsentList(long eventId) {
        long guideAbsent = queryFactory.select(attendance.count())
                .from(attendance, user)
                .where(attendance.isAttend.eq(false),
                        attendance.eventId.eq(eventId),
                        user.privateId.eq(attendance.privateId),
                        user.type.eq(UserType.GUIDE))
                .fetchOne();
        long viAbsent = queryFactory.select(attendance.count())
                .from(attendance, user)
                .where(attendance.isAttend.eq(false),
                        attendance.eventId.eq(eventId),
                        user.privateId.eq(attendance.privateId),
                        user.type.eq(UserType.VI))
                .fetchOne();


        return AbsentDto.builder()
                .absent(guideAbsent+viAbsent)
                .guideAbsent(guideAbsent)
                .viAbsent(viAbsent)
                .build();
    }

}
