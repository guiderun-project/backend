package com.guide.run.event.service;

import com.guide.run.event.entity.repository.EventFormRepository;
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
class EventGetServiceTest {

    @Mock
    private EventRepository eventRepository;
    @Mock
    private EventFormRepository eventFormRepository;

    @InjectMocks
    private EventGetService eventGetService;

    @Test
    @DisplayName("예정 이벤트 카운트는 시작일 필터가 포함된 전용 쿼리를 사용한다")
    void getAllEventListCountUsesUpcomingCountQuery() {
        when(eventRepository.countUpcomingEventList(null, EventRecruitStatus.RECRUIT_ALL, null))
                .thenReturn(7L);

        long count = eventGetService.getAllEventListCount(
                "UPCOMING",
                EventType.TOTAL,
                EventRecruitStatus.RECRUIT_ALL,
                null,
                null
        );

        assertThat(count).isEqualTo(7L);
        verify(eventRepository).countUpcomingEventList(null, EventRecruitStatus.RECRUIT_ALL, null);
        verify(eventRepository, never()).countByRecruitStatusNotAndIsApprove(EventRecruitStatus.RECRUIT_END, true);
    }

    @Test
    @DisplayName("지난 이벤트 카운트는 공개 이벤트 전용 쿼리를 사용한다")
    void getAllEventListCountUsesPublicEventCountQueryForEndedEvents() {
        when(eventRepository.countEventList(null, EventRecruitStatus.RECRUIT_END, null))
                .thenReturn(5L);

        long count = eventGetService.getAllEventListCount(
                "END",
                EventType.TOTAL,
                EventRecruitStatus.RECRUIT_ALL,
                null,
                null
        );

        assertThat(count).isEqualTo(5L);
        verify(eventRepository).countEventList(null, EventRecruitStatus.RECRUIT_END, null);
        verify(eventRepository, never()).countByRecruitStatusAndIsApprove(EventRecruitStatus.RECRUIT_END, true);
    }
}
