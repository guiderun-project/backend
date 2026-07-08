package com.guide.run.event.service;

import com.guide.run.event.entity.Event;
import com.guide.run.event.entity.dto.response.get.AllEvent;
import com.guide.run.event.entity.repository.EventFormRepository;
import com.guide.run.event.entity.repository.EventRepository;
import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.event.entity.type.EventType;
import com.guide.run.partner.entity.matching.repository.MatchingRepository;
import com.guide.run.user.entity.type.Role;
import com.guide.run.user.entity.type.UserType;
import com.guide.run.user.entity.user.User;
import com.guide.run.user.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventGetServiceTest {

    @Mock
    private EventRepository eventRepository;
    @Mock
    private EventFormRepository eventFormRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MatchingRepository matchingRepository;

    @InjectMocks
    private EventGetService eventGetService;

    @Test
    @DisplayName("예정 이벤트 카운트는 종료 전 필터가 포함된 전용 쿼리를 사용한다")
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
    @DisplayName("지난 이벤트 카운트는 종료 시간 필터가 포함된 전용 쿼리를 사용한다")
    void getAllEventListCountUsesPastCountQueryForEndedEvents() {
        when(eventRepository.countPastEventList(null, null))
                .thenReturn(5L);

        long count = eventGetService.getAllEventListCount(
                "END",
                EventType.TOTAL,
                EventRecruitStatus.RECRUIT_ALL,
                null,
                null
        );

        assertThat(count).isEqualTo(5L);
        verify(eventRepository).countPastEventList(null, null);
        verify(eventRepository, never()).countEventList(null, EventRecruitStatus.RECRUIT_END, null);
    }

    @Test
    @DisplayName("지난 이벤트 목록은 종료 시간 필터가 포함된 전용 쿼리를 사용한다")
    void getAllEventListUsesPastListQueryForEndedEvents() {
        List<AllEvent> pastEvents = List.of(new AllEvent(
                1L,
                EventType.TRAINING,
                "지난 이벤트",
                LocalDateTime.of(2026, 6, 1, 9, 0),
                EventRecruitStatus.RECRUIT_OPEN
        ));
        when(eventRepository.pastGetAllEventList(10, 0, null, null))
                .thenReturn(pastEvents);
        when(eventRepository.countPastEventList(null, null))
                .thenReturn(1L);

        var response = eventGetService.getAllEventList(
                10,
                0,
                1,
                "END",
                EventType.TOTAL,
                EventRecruitStatus.RECRUIT_ALL,
                null,
                null
        );

        assertThat(response.getItems()).hasSize(1);
        verify(eventRepository).pastGetAllEventList(10, 0, null, null);
        verify(eventRepository, never()).getAllEventList(10, 0, null, EventRecruitStatus.RECRUIT_END, null);
    }

    @Test
    @DisplayName("회원 다가오는 이벤트는 저장 상태가 이벤트 종료여도 시작 시간이 미래면 반환한다")
    void getUpcomingEventsIncludesFutureEventWithStaleRecruitEndStatus() {
        User member = User.builder()
                .privateId("member-private")
                .role(Role.ROLE_USER)
                .type(UserType.GUIDE)
                .build();
        Event futureEvent = createEvent(
                1L,
                EventRecruitStatus.RECRUIT_END,
                EventTemporalStatusResolver.now().plusDays(2)
        );

        when(userRepository.findUserByPrivateId("member-private")).thenReturn(Optional.of(member));
        when(eventFormRepository.findAllByPrivateId("member-private")).thenReturn(List.of());
        when(eventRepository.findAllById(any())).thenReturn(List.of());
        when(eventRepository.findAllByOrganizer("member-private")).thenReturn(List.of(futureEvent));

        var response = eventGetService.getUpcomingEvents("member-private");

        assertThat(response.getItems()).hasSize(1);
    }

    @Test
    @DisplayName("회원 다가오는 이벤트는 이미 시작했어도 종료 전이면 반환한다")
    void getUpcomingEventsIncludesStartedEventBeforeEndTime() {
        User member = User.builder()
                .privateId("member-private")
                .role(Role.ROLE_USER)
                .type(UserType.GUIDE)
                .build();
        Event startedEvent = createEvent(
                1L,
                EventRecruitStatus.RECRUIT_OPEN,
                EventTemporalStatusResolver.now().minusMinutes(1)
        );

        when(userRepository.findUserByPrivateId("member-private")).thenReturn(Optional.of(member));
        when(eventFormRepository.findAllByPrivateId("member-private")).thenReturn(List.of());
        when(eventRepository.findAllById(any())).thenReturn(List.of());
        when(eventRepository.findAllByOrganizer("member-private")).thenReturn(List.of(startedEvent));

        var response = eventGetService.getUpcomingEvents("member-private");

        assertThat(response.getItems()).hasSize(1);
    }

    private Event createEvent(Long id, EventRecruitStatus recruitStatus, LocalDateTime startTime) {
        return Event.builder()
                .id(id)
                .name("상계천천히달리기")
                .organizer("member-private")
                .recruitStatus(recruitStatus)
                .isApprove(true)
                .type(EventType.TRAINING)
                .startTime(startTime)
                .endTime(startTime.plusHours(2))
                .place("서울")
                .build();
    }
}
