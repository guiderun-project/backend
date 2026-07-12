package com.guide.run.event.entity.repository;

import com.querydsl.core.BooleanBuilder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.RETURNS_SELF;

class EventRepositoryImplTest {

    @Test
    @DisplayName("예정 이벤트 조건은 시작 전이 아니라 종료 전 이벤트를 대상으로 한다")
    void upcomingDatePredicateUsesEndTime() throws Exception {
        EventRepositoryImpl repository = new EventRepositoryImpl(mock(EntityManager.class));
        Method method = EventRepositoryImpl.class.getDeclaredMethod("checkByNotEndedDateTime");
        method.setAccessible(true);

        BooleanBuilder predicate = (BooleanBuilder) method.invoke(repository);

        assertThat(predicate.toString())
                .contains("event.endTime")
                .doesNotContain("event.startTime");
    }

    @Test
    @DisplayName("올해 전체 러닝 거리 합산은 예상 러닝 거리 컬럼을 사용한다")
    void sumDistanceByYearUsesExpectedRunningDistanceKm() {
        EntityManager entityManager = mock(EntityManager.class);
        stubSingleResultQuery(entityManager, null);
        EventRepositoryImpl repository = new EventRepositoryImpl(entityManager);

        repository.sumDistanceByYear(2026);

        String jpql = captureJpql(entityManager);
        assertThat(jpql)
                .contains("sum(event.expectedRunningDistanceKm)")
                .doesNotContain("sum(event.distance)");
    }

    @Test
    @DisplayName("개인 참여 수는 종료된 신청 상태 이벤트만 집계한다")
    void countMyParticipationUsesEndedAppliedEvents() {
        EntityManager entityManager = mock(EntityManager.class);
        stubSingleResultQuery(entityManager, null);
        EventRepositoryImpl repository = new EventRepositoryImpl(entityManager);

        repository.countMyParticipation("member-private");

        String jpql = captureJpql(entityManager);
        assertThat(jpql)
                .contains("event.endTime <")
                .contains("eventForm.status");
    }

    @Test
    @DisplayName("개인 참여 거리는 종료된 신청 상태 이벤트의 예상 러닝 거리를 합산한다")
    void sumMyParticipationDistanceUsesExpectedRunningDistanceKmForEndedAppliedEvents() {
        EntityManager entityManager = mock(EntityManager.class);
        stubSingleResultQuery(entityManager, null);
        EventRepositoryImpl repository = new EventRepositoryImpl(entityManager);

        repository.sumMyParticipationDistance("member-private");

        String jpql = captureJpql(entityManager);
        assertThat(jpql)
                .contains("sum(event.expectedRunningDistanceKm)")
                .contains("event.endTime <")
                .contains("eventForm.status")
                .doesNotContain("sum(event.distance)");
    }

    @Test
    @DisplayName("참여 활동 목록은 신청 상태 이벤트만 조회한다")
    void activityParticipatedEventsUseAppliedForms() {
        EntityManager entityManager = mock(EntityManager.class);
        stubSingleResultQuery(entityManager, null);
        EventRepositoryImpl repository = new EventRepositoryImpl(entityManager);

        repository.findActivityEvents("member-private", null, "PARTICIPATED", 0, 10);

        String jpql = captureJpql(entityManager);
        assertThat(jpql)
                .contains("eventForm.status")
                .contains("exists");
    }

    @Test
    @DisplayName("전체 활동 목록도 신청 상태 이벤트만 조회한다")
    void totalActivityEventsUseAppliedForms() {
        EntityManager entityManager = mock(EntityManager.class);
        stubSingleResultQuery(entityManager, null);
        EventRepositoryImpl repository = new EventRepositoryImpl(entityManager);

        repository.findActivityEvents("member-private", null, "TOTAL", 0, 10);

        String jpql = captureJpql(entityManager);
        assertThat(jpql)
                .contains("eventForm.status")
                .contains("exists");
    }

    private Query stubSingleResultQuery(EntityManager entityManager, Object result) {
        EntityManagerFactory entityManagerFactory = mock(EntityManagerFactory.class);
        when(entityManagerFactory.getProperties()).thenReturn(Map.of());
        when(entityManager.getEntityManagerFactory()).thenReturn(entityManagerFactory);

        Query query = mock(Query.class, RETURNS_SELF);
        when(query.getSingleResult()).thenReturn(result);
        when(query.getResultList()).thenReturn(List.of());
        when(entityManager.createQuery(anyString())).thenReturn(query);
        return query;
    }

    private String captureJpql(EntityManager entityManager) {
        ArgumentCaptor<String> jpqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(entityManager).createQuery(jpqlCaptor.capture());
        return jpqlCaptor.getValue();
    }
}
