package com.guide.run.temp.member.repository;

import com.guide.run.event.entity.dto.response.attend.ParticipationInfo;
import com.guide.run.temp.member.entity.Attendance;
import com.guide.run.user.entity.type.UserType;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.List;

import static com.guide.run.event.entity.QEventForm.eventForm;
import static com.guide.run.temp.member.entity.QAttendance.*;
import static com.guide.run.user.entity.user.QUser.*;

public class AttendanceRepositoryImpl implements AttendanceCustomRepository{
    private final JPAQueryFactory queryFactory;

    public AttendanceRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Long countUserType(Long eventId, UserType userType) {
        return (long) queryFactory.select(attendance.count())
                .from(attendance)
                .join(user).on(attendance.privateId.eq(user.privateId))
                .where(user.type.eq(userType).and(attendance.eventId.eq(eventId)))
                .fetchOne();
    }

    @Override
    public List<ParticipationInfo > getParticipationInfo(Long eventId, boolean isAttend) {
        return queryFactory.select(Projections.constructor(ParticipationInfo.class,
                user.userId.as("userId"),
                user.type.as("type"),
                eventForm.hopeTeam.as("applyRecord"),
                user.name.as("name")))
                .from(attendance)
                .join(user).on(attendance.privateId.eq(user.privateId))
                .join(eventForm).on(attendance.privateId.eq(eventForm.privateId)).on(user.privateId.eq(eventForm.privateId))
                .where(attendance.isAttend.eq(isAttend).and(attendance.eventId.eq(eventId)))
                .orderBy(user.type.desc())
                .fetch();
    }

    @Override
    public List<Attendance> getAttendanceTrue(Long eventId, boolean isAttend) {
        return queryFactory.selectFrom(attendance)
                .where(attendance.isAttend.eq(isAttend)
                        ,attendance.eventId.eq(eventId))
                .fetch();
    }
}
