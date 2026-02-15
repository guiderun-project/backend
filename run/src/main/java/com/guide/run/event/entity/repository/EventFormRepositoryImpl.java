package com.guide.run.event.entity.repository;

import com.guide.run.event.entity.dto.response.form.Form;
import com.guide.run.event.entity.dto.response.form.FormWithPhone;
import com.guide.run.user.entity.type.UserType;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.List;

import static com.guide.run.event.entity.QEventForm.eventForm;
import static com.guide.run.user.entity.user.QUser.user;

public class EventFormRepositoryImpl implements EventFormRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    public EventFormRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Form> findAllEventIdAndUserType(Long eventId, UserType userType) {
        return queryFactory.select(Projections.constructor(Form.class,
                user.userId.as("userId"),
                user.type.as("type"),
                eventForm.hopeTeam.as("applyRecord"),
                user.name.as("name"),
                user.recordDegree.as("recordDegree")))
                .from(eventForm)
                .join(user).on(eventForm.privateId.eq(user.privateId).and(eventForm.eventId.eq(eventId)))
                .where(eventForm.eventId.eq(eventId).and(user.type.eq(userType)))
                .fetch();
    }
    @Override
    public List<Form> findAllEventIdAndUserTypeAndHopeTeam(Long eventId, UserType userType,String hopeTeam) {
        return queryFactory.select(Projections.constructor(Form.class,
                        user.userId.as("userId"),
                        user.type.as("type"),
                        eventForm.hopeTeam.as("applyRecord"),
                        user.name.as("name"),
                        user.recordDegree.as("recordDegree")))
                .from(eventForm)
                .join(user).on(eventForm.privateId.eq(user.privateId).and(eventForm.eventId.eq(eventId)))
                .where(eventForm.eventId.eq(eventId).and(user.type.eq(userType)).and(eventForm.hopeTeam.eq(hopeTeam)))
                .fetch();
    }

    @Override
    public List<FormWithPhone> findAllFormsWithPhone(Long eventId, UserType userType) {
        return queryFactory.select(Projections.constructor(FormWithPhone.class,
                        user.userId.as("userId"),
                        user.type.as("type"),
                        eventForm.hopeTeam.as("applyRecord"),
                        user.name.as("name"),
                        user.recordDegree.as("recordDegree"),
                        user.phoneNumber.as("phone")))
                .from(eventForm)
                .join(user).on(eventForm.privateId.eq(user.privateId).and(eventForm.eventId.eq(eventId)))
                .where(eventForm.eventId.eq(eventId).and(user.type.eq(userType)))
                .orderBy(user.name.asc())
                .fetch();
    }

    @Override
    public List<FormWithPhone> findAllFormsWithoutPhone(Long eventId, UserType userType) {
        return queryFactory.select(Projections.constructor(FormWithPhone.class,
                        user.userId.as("userId"),
                        user.type.as("type"),
                        eventForm.hopeTeam.as("applyRecord"),
                        user.name.as("name"),
                        user.recordDegree.as("recordDegree")))
                .from(eventForm)
                .join(user).on(eventForm.privateId.eq(user.privateId).and(eventForm.eventId.eq(eventId)))
                .where(eventForm.eventId.eq(eventId).and(user.type.eq(userType)))
                .orderBy(user.name.asc())
                .fetch();
    }
}
