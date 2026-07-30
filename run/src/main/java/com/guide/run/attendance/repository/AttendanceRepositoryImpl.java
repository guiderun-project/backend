package com.guide.run.attendance.repository;


import static com.guide.run.attendance.entity.QAttendance.attendance;
import static com.guide.run.user.entity.user.QUser.user;

import com.guide.run.event.entity.dto.response.attend.AttendanceParticipant;
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
    public List<AttendanceParticipant> findAttendanceParticipants(Long eventId, boolean isAttend) {
        return queryFactory.select(Projections.constructor(AttendanceParticipant.class,
                user.userId.as("userId"),
                user.name.as("name"),
                user.type.as("type"),
                user.trainingCnt.add(user.competitionCnt).eq(0)))
                .from(attendance)
                .join(user).on(attendance.privateId.eq(user.privateId))
                .where(attendance.isAttend.eq(isAttend).and(attendance.eventId.eq(eventId)))
                .orderBy(user.type.desc(), user.name.asc())
                .fetch();
    }

}
