package com.guide.run.event.service;

import com.guide.run.event.entity.dto.response.get.AllEvent;
import com.guide.run.event.entity.repository.EventRepository;
import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.event.entity.type.EventType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventSearchServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventSearchService eventSearchService;

    @Test
    @DisplayName("예정 이벤트 검색 카운트는 종료 전 필터가 포함된 전용 쿼리를 사용한다")
    void getSearchAllEventsCountValueUsesUpcomingSearchCountQuery() {
        when(eventRepository.upcomingGetSearchEventListCount("러닝", null, EventRecruitStatus.RECRUIT_ALL, null))
                .thenReturn(3L);

        long count = eventSearchService.getSearchAllEventsCountValue(
                "러닝",
                "UPCOMING",
                EventType.TOTAL,
                EventRecruitStatus.RECRUIT_ALL,
                null,
                null
        );

        assertThat(count).isEqualTo(3L);
        verify(eventRepository).upcomingGetSearchEventListCount("러닝", null, EventRecruitStatus.RECRUIT_ALL, null);
        verify(eventRepository, never()).getSearchEventListCount("러닝", null, EventRecruitStatus.RECRUIT_ALL, null);
    }

    @Test
    @DisplayName("지난 이벤트 검색 카운트는 종료 시간 필터가 포함된 전용 쿼리를 사용한다")
    void getSearchAllEventsCountValueUsesPastSearchCountQuery() {
        when(eventRepository.pastGetSearchEventListCount("러닝", null, null))
                .thenReturn(4L);

        long count = eventSearchService.getSearchAllEventsCountValue(
                "러닝",
                "END",
                EventType.TOTAL,
                EventRecruitStatus.RECRUIT_ALL,
                null,
                null
        );

        assertThat(count).isEqualTo(4L);
        verify(eventRepository).pastGetSearchEventListCount("러닝", null, null);
        verify(eventRepository, never()).getSearchEventListCount("러닝", null, EventRecruitStatus.RECRUIT_END, null);
    }

    @Test
    @DisplayName("지난 이벤트 검색 목록은 종료 시간 필터가 포함된 전용 쿼리를 사용한다")
    void getSearchAllEventsUsesPastSearchListQuery() {
        List<AllEvent> pastEvents = List.of(new AllEvent(
                1L,
                EventType.TRAINING,
                "지난 이벤트",
                LocalDateTime.of(2026, 6, 1, 9, 0),
                EventRecruitStatus.RECRUIT_OPEN
        ));
        when(eventRepository.pastGetSearchEventList(10, 0, "러닝", null, null))
                .thenReturn(pastEvents);
        when(eventRepository.pastGetSearchEventListCount("러닝", null, null))
                .thenReturn(1L);

        var response = eventSearchService.getSearchAllEvents(
                0,
                10,
                1,
                "러닝",
                "END",
                EventType.TOTAL,
                EventRecruitStatus.RECRUIT_ALL,
                null,
                null
        );

        assertThat(response.getItems()).hasSize(1);
        verify(eventRepository).pastGetSearchEventList(10, 0, "러닝", null, null);
        verify(eventRepository, never()).getSearchEventList(10, 0, "러닝", null, EventRecruitStatus.RECRUIT_END, null);
    }
}
