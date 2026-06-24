package com.guide.run.event.service;

import com.guide.run.event.entity.repository.EventRepository;
import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.event.entity.type.EventType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    @DisplayName("예정 이벤트 검색 카운트는 시작일 필터가 포함된 전용 쿼리를 사용한다")
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
}
