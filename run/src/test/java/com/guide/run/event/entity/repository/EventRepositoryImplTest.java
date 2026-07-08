package com.guide.run.event.entity.repository;

import com.querydsl.core.BooleanBuilder;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

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
}
