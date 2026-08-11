package com.guide.run.event.service;

import com.guide.run.event.entity.dto.response.get.AllEvent;
import com.guide.run.event.entity.repository.EventRepository;
import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.event.entity.type.EventType;
import com.guide.run.user.entity.type.Role;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventSearchServiceTest {

    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EventSearchService eventSearchService;

    @Test
    @DisplayName("예정 이벤트 검색 카운트는 종료 전 필터가 포함된 전용 쿼리를 사용한다")
    void getSearchAllEventsCountValueUsesUpcomingSearchCountQuery() {
        when(eventRepository.upcomingGetSearchEventListCount("러닝", null, EventRecruitStatus.RECRUIT_ALL, null, false))
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
        verify(eventRepository).upcomingGetSearchEventListCount("러닝", null, EventRecruitStatus.RECRUIT_ALL, null, false);
        verify(eventRepository, never()).getSearchEventListCount("러닝", null, EventRecruitStatus.RECRUIT_ALL, null);
    }

    @Test
    @DisplayName("지난 이벤트 검색 카운트는 종료 시간 필터가 포함된 전용 쿼리를 사용한다")
    void getSearchAllEventsCountValueUsesPastSearchCountQuery() {
        when(eventRepository.pastGetSearchEventListCount("러닝", null, null, false))
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
        verify(eventRepository).pastGetSearchEventListCount("러닝", null, null, false);
        verify(eventRepository, never()).getSearchEventListCount("러닝", null, EventRecruitStatus.RECRUIT_END, null);
    }

    @Test
    @DisplayName("예정 이벤트 검색은 관리자면 비공개 이벤트를 포함해 조회한다")
    void getSearchAllEventsIncludesPrivateEventsForAdminOnUpcomingTab() {
        User admin = User.builder()
                .privateId("admin-private")
                .role(Role.ROLE_ADMIN)
                .build();
        when(userRepository.findUserByPrivateId("admin-private")).thenReturn(Optional.of(admin));

        eventSearchService.getSearchAllEvents(
                0,
                10,
                1,
                "러닝",
                "UPCOMING",
                EventType.TOTAL,
                EventRecruitStatus.RECRUIT_ALL,
                "admin-private",
                null
        );

        verify(eventRepository).upcomingGetSearchEventList(10, 0, "러닝", null, EventRecruitStatus.RECRUIT_ALL, null, true);
        verify(eventRepository).upcomingGetSearchEventListCount("러닝", null, EventRecruitStatus.RECRUIT_ALL, null, true);
    }

    @Test
    @DisplayName("종료 이벤트 검색은 관리자면 비공개 이벤트를 포함해 조회한다")
    void getSearchAllEventsIncludesPrivateEventsForAdminOnEndTab() {
        User admin = User.builder()
                .privateId("admin-private")
                .role(Role.ROLE_ADMIN)
                .build();
        when(userRepository.findUserByPrivateId("admin-private")).thenReturn(Optional.of(admin));

        eventSearchService.getSearchAllEvents(
                0,
                10,
                1,
                "러닝",
                "END",
                EventType.TOTAL,
                EventRecruitStatus.RECRUIT_ALL,
                "admin-private",
                null
        );

        verify(eventRepository).pastGetSearchEventList(10, 0, "러닝", null, null, true);
        verify(eventRepository).pastGetSearchEventListCount("러닝", null, null, true);
    }

    @Test
    @DisplayName("종료 이벤트 검색은 일반 회원이면 비공개 이벤트를 제외한다")
    void getSearchAllEventsExcludesPrivateEventsForNonAdminMember() {
        User member = User.builder()
                .privateId("member-private")
                .role(Role.ROLE_USER)
                .build();
        when(userRepository.findUserByPrivateId("member-private")).thenReturn(Optional.of(member));

        eventSearchService.getSearchAllEvents(
                0,
                10,
                1,
                "러닝",
                "END",
                EventType.TOTAL,
                EventRecruitStatus.RECRUIT_ALL,
                "member-private",
                null
        );

        verify(eventRepository).pastGetSearchEventList(10, 0, "러닝", null, null, false);
        verify(eventRepository).pastGetSearchEventListCount("러닝", null, null, false);
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
        when(eventRepository.pastGetSearchEventList(10, 0, "러닝", null, null, false))
                .thenReturn(pastEvents);
        when(eventRepository.pastGetSearchEventListCount("러닝", null, null, false))
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
        verify(eventRepository).pastGetSearchEventList(10, 0, "러닝", null, null, false);
        verify(eventRepository, never()).getSearchEventList(10, 0, "러닝", null, EventRecruitStatus.RECRUIT_END, null);
    }
}
