package com.guide.run.attendance.repository;


import static com.guide.run.attendance.entity.QAttendance.attendance;
import static com.guide.run.event.entity.QEventForm.eventForm;
import static com.guide.run.user.entity.user.QUser.user;

import com.guide.run.attendance.entity.QAttendance;
import com.guide.run.event.entity.dto.response.attend.ParticipationInfo;
import com.guide.run.attendance.entity.Attendance;
import com.guide.run.user.entity.type.UserType;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.List;



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
                user.recordDegree.as("recordDegree"),
                user.name.as("name")))
                .from(attendance)
                .join(user).on(attendance.privateId.eq(user.privateId))
                .join(eventForm).on(attendance.privateId.eq(eventForm.privateId).and(eventForm.eventId.eq(eventId)))
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
