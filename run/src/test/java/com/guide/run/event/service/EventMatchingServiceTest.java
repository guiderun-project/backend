package com.guide.run.event.service;

import com.guide.run.attendance.repository.AttendanceRepository;
import com.guide.run.event.entity.Event;
import com.guide.run.event.entity.EventForm;
import com.guide.run.event.entity.dto.response.match.EventMatchingStatusResponse;
import com.guide.run.event.entity.dto.response.match.MatchingCompletedFlatDto;
import com.guide.run.event.entity.dto.response.match.MatchingCompletedResponse;
import com.guide.run.event.entity.dto.response.match.MatchingStatusGroup;
import com.guide.run.event.entity.dto.response.match.MatchingWaitingResponse;
import com.guide.run.event.entity.dto.request.match.MatchingCreateRequest;
import com.guide.run.event.entity.dto.response.match.MatchingWaitingFlatDto;
import com.guide.run.event.entity.repository.EventFormRepository;
import com.guide.run.global.exception.event.logic.EventValidationException;
import com.guide.run.event.entity.repository.EventRepository;
import com.guide.run.event.entity.type.EventFormStatus;
import com.guide.run.partner.entity.matching.Matching;
import com.guide.run.partner.entity.matching.UnMatching;
import com.guide.run.partner.entity.matching.repository.MatchingRepository;
import com.guide.run.partner.entity.matching.repository.UnMatchingRepository;
import com.guide.run.partner.service.PartnerService;
import com.guide.run.user.entity.type.UserType;
import com.guide.run.user.entity.user.User;
import com.guide.run.user.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventMatchingServiceTest {
    @Mock
    private UnMatchingRepository unMatchingRepository;
    @Mock
    private MatchingRepository matchingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private EventFormRepository eventFormRepository;
    @Mock
    private PartnerService partnerService;
    @Mock
    private AttendanceRepository attendanceRepository;

    @InjectMocks
    private EventMatchingService eventMatchingService;

    @Test
    @DisplayName("수동 매칭은 취소 이력이 있어도 APPLIED 신청서를 기준으로 생성한다")
    void matchUserUsesAppliedFormsWhenCanceledHistoryExists() {
        User vi = User.builder()
                .privateId("vi-private")
                .userId("vi-user")
                .type(UserType.VI)
                .build();
        User guide = User.builder()
                .privateId("guide-private")
                .userId("guide-user")
                .type(UserType.GUIDE)
                .build();
        EventForm canceledViForm = EventForm.builder()
                .eventId(1L)
                .privateId("vi-private")
                .hopeTeam("VI-CANCELED")
                .status(EventFormStatus.CANCELED)
                .build();
        EventForm canceledGuideForm = EventForm.builder()
                .eventId(1L)
                .privateId("guide-private")
                .hopeTeam("GUIDE-CANCELED")
                .status(EventFormStatus.CANCELED)
                .build();
        EventForm appliedViForm = EventForm.builder()
                .eventId(1L)
                .privateId("vi-private")
                .hopeTeam("VI-APPLIED")
                .status(EventFormStatus.APPLIED)
                .build();
        EventForm appliedGuideForm = EventForm.builder()
                .eventId(1L)
                .privateId("guide-private")
                .hopeTeam("GUIDE-APPLIED")
                .status(EventFormStatus.APPLIED)
                .build();

        when(eventRepository.findById(1L)).thenReturn(Optional.of(Event.builder().id(1L).build()));
        when(userRepository.findUserByUserId("vi-user")).thenReturn(Optional.of(vi));
        when(userRepository.findUserByUserId("guide-user")).thenReturn(Optional.of(guide));
        lenient().when(eventFormRepository.findByEventIdAndPrivateId(1L, "vi-private"))
                .thenReturn(canceledViForm);
        lenient().when(eventFormRepository.findByEventIdAndPrivateId(1L, "guide-private"))
                .thenReturn(canceledGuideForm);
        lenient().when(eventFormRepository.findByEventIdAndPrivateIdAndStatus(1L, "vi-private", EventFormStatus.APPLIED))
                .thenReturn(appliedViForm);
        lenient().when(eventFormRepository.findByEventIdAndPrivateIdAndStatus(1L, "guide-private", EventFormStatus.APPLIED))
                .thenReturn(appliedGuideForm);
        when(matchingRepository.findByEventIdAndGuideId(1L, "guide-private")).thenReturn(null);
        when(unMatchingRepository.findByPrivateIdAndEventId("vi-private", 1L)).thenReturn(Optional.empty());

        eventMatchingService.matchUser(1L, "vi-user", "guide-user");

        ArgumentCaptor<Matching> matchingCaptor = ArgumentCaptor.forClass(Matching.class);
        verify(matchingRepository).save(matchingCaptor.capture());
        Matching savedMatching = matchingCaptor.getValue();
        assertThat(savedMatching.getViRecord()).isEqualTo("VI-APPLIED");
        assertThat(savedMatching.getGuideRecord()).isEqualTo("GUIDE-APPLIED");
        verify(eventFormRepository).findByEventIdAndPrivateIdAndStatus(1L, "vi-private", EventFormStatus.APPLIED);
        verify(eventFormRepository).findByEventIdAndPrivateIdAndStatus(1L, "guide-private", EventFormStatus.APPLIED);
    }

    @Test
    @DisplayName("매칭 현황은 미매칭 VI도 신청 그룹의 독립 행으로 반환한다")
    void getMatchingStatusIncludesUnmatchedViRows() {
        User loginUser = User.builder()
                .privateId("login-guide-private")
                .userId("login-guide-user")
                .type(UserType.GUIDE)
                .build();
        MatchingWaitingFlatDto waitingVi = new MatchingWaitingFlatDto(
                "vi-user",
                "김민지",
                UserType.VI,
                "A",
                "상관없음",
                "천천히 출발하고 싶습니다.",
                0,
                0
        );
        MatchingWaitingFlatDto waitingGuide = new MatchingWaitingFlatDto(
                "guide-user",
                "정현우",
                UserType.GUIDE,
                "A",
                "상관없음",
                "안전하게 보조 가능합니다.",
                0,
                0
        );

        when(eventRepository.findById(1L)).thenReturn(Optional.of(Event.builder().id(1L).build()));
        when(userRepository.findUserByPrivateId("login-guide-private")).thenReturn(Optional.of(loginUser));
        when(matchingRepository.findByEventIdAndGuideId(1L, "login-guide-private")).thenReturn(null);
        when(matchingRepository.findMatchingCompletedByEventId(1L)).thenReturn(List.of());
        when(unMatchingRepository.findWaitingParticipants(1L)).thenReturn(List.of(waitingVi, waitingGuide));

        EventMatchingStatusResponse response = eventMatchingService.getMatchingStatus(1L, "login-guide-private");

        assertThat(response.getGroups()).hasSize(1);
        MatchingStatusGroup group = response.getGroups().get(0);
        assertThat(group.getRunningGroup()).isEqualTo("A");
        assertThat(group.getTotalCount()).isEqualTo(2);
        assertThat(group.getRows())
                .anySatisfy(row -> {
                    assertThat(row.getVi()).isNotNull();
                    assertThat(row.getVi().getUserId()).isEqualTo("vi-user");
                    assertThat(row.getVi().getType()).isEqualTo(UserType.VI);
                    assertThat(row.getGuides()).isEmpty();
                })
                .anySatisfy(row -> {
                    assertThat(row.getVi()).isNull();
                    assertThat(row.getGuides()).hasSize(1);
                    assertThat(row.getGuides().get(0).getUserId()).isEqualTo("guide-user");
                    assertThat(row.getGuides().get(0).getType()).isEqualTo(UserType.GUIDE);
                });
    }

    @Test
    @DisplayName("매칭 현황은 미매칭 참가자를 VI 먼저, Guide 나중, 각 타입 가나다순으로 반환한다")
    void getMatchingStatusSortsUnmatchedRowsByTypeAndName() {
        User loginUser = User.builder()
                .privateId("login-guide-private")
                .userId("login-guide-user")
                .type(UserType.GUIDE)
                .build();

        when(eventRepository.findById(1L)).thenReturn(Optional.of(Event.builder().id(1L).build()));
        when(userRepository.findUserByPrivateId("login-guide-private")).thenReturn(Optional.of(loginUser));
        when(matchingRepository.findByEventIdAndGuideId(1L, "login-guide-private")).thenReturn(null);
        when(matchingRepository.findMatchingCompletedByEventId(1L)).thenReturn(List.of());
        when(unMatchingRepository.findWaitingParticipants(1L)).thenReturn(unsortedWaitingParticipants());

        EventMatchingStatusResponse response = eventMatchingService.getMatchingStatus(1L, "login-guide-private");

        List<String> orderedNames = response.getGroups().get(0).getRows().stream()
                .map(row -> row.getVi() != null ? row.getVi().getName() : row.getGuides().get(0).getName())
                .toList();
        List<UserType> orderedTypes = response.getGroups().get(0).getRows().stream()
                .map(row -> row.getVi() != null ? row.getVi().getType() : row.getGuides().get(0).getType())
                .toList();
        assertThat(orderedNames).containsExactly("김시각", "최시각", "김가이드", "하가이드");
        assertThat(orderedTypes).containsExactly(UserType.VI, UserType.VI, UserType.GUIDE, UserType.GUIDE);
    }

    @Test
    @DisplayName("매칭 현황 그룹 totalCount는 row 수가 아니라 그룹에 표시되는 전체 인원 수를 반환한다")
    void getMatchingStatusCountsPeopleInGroup() {
        User loginUser = User.builder()
                .privateId("login-guide-private")
                .userId("login-guide-user")
                .type(UserType.GUIDE)
                .build();

        when(eventRepository.findById(1L)).thenReturn(Optional.of(Event.builder().id(1L).build()));
        when(userRepository.findUserByPrivateId("login-guide-private")).thenReturn(Optional.of(loginUser));
        when(matchingRepository.findByEventIdAndGuideId(1L, "login-guide-private")).thenReturn(null);
        when(matchingRepository.findMatchingCompletedByEventId(1L)).thenReturn(List.of(
                matchedFlat("vi-a", "김시각", "A", "guide-b", "박가이드", "B"),
                matchedFlat("vi-a", "김시각", "A", "guide-c", "이가이드", "C")
        ));
        when(unMatchingRepository.findWaitingParticipants(1L)).thenReturn(List.of());

        EventMatchingStatusResponse response = eventMatchingService.getMatchingStatus(1L, "login-guide-private");

        assertThat(response.getGroups()).hasSize(1);
        assertThat(response.getGroups().get(0).getRunningGroup()).isEqualTo("A");
        assertThat(response.getGroups().get(0).getRows()).hasSize(1);
        assertThat(response.getGroups().get(0).getTotalCount()).isEqualTo(3);
    }

    @Test
    @DisplayName("매칭 완료 그룹 totalCount는 row 수가 아니라 그룹에 표시되는 전체 인원 수를 반환한다")
    void getMatchingCompletedCountsPeopleInGroup() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(Event.builder().id(1L).build()));
        when(matchingRepository.findMatchingCompletedByEventId(1L)).thenReturn(List.of(
                matchedFlat("vi-a", "김시각", "A", "guide-b", "박가이드", "B"),
                matchedFlat("vi-a", "김시각", "A", "guide-c", "이가이드", "C")
        ));

        MatchingCompletedResponse response = eventMatchingService.getMatchingCompleted(1L);

        assertThat(response.getGroups()).hasSize(1);
        assertThat(response.getGroups().get(0).getRunningGroup()).isEqualTo("A");
        assertThat(response.getGroups().get(0).getRows()).hasSize(1);
        assertThat(response.getGroups().get(0).getTotalCount()).isEqualTo(3);
        assertThat(response.getSummary().getCompletedViCount()).isEqualTo(1);
        assertThat(response.getSummary().getMatchedGuideCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("매칭 대기는 미매칭 참가자를 VI 먼저, Guide 나중, 각 타입 가나다순으로 반환한다")
    void getMatchingWaitingSortsParticipantsByTypeAndName() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(Event.builder().id(1L).build()));
        when(unMatchingRepository.findWaitingParticipants(1L)).thenReturn(unsortedWaitingParticipants());

        MatchingWaitingResponse response = eventMatchingService.getMatchingWaiting(1L);

        assertThat(response.getGroups()).hasSize(1);
        assertThat(response.getGroups().get(0).getParticipants())
                .extracting("name")
                .containsExactly("김시각", "최시각", "김가이드", "하가이드");
        assertThat(response.getGroups().get(0).getParticipants())
                .extracting("type")
                .containsExactly(UserType.VI, UserType.VI, UserType.GUIDE, UserType.GUIDE);
    }

    @Test
    @DisplayName("매칭 생성은 replace 시맨틱: 요청 guideIds 에 없는 기존 가이드를 해제하고 대기로 전환한다")
    void createMatchingReplacesGuideSet() {
        User vi = User.builder()
                .privateId("vi-private").userId("vi-user").type(UserType.VI).build();
        User g1 = User.builder()
                .privateId("g1-private").userId("g1-user").type(UserType.GUIDE).build();
        User g2 = User.builder()
                .privateId("g2-private").userId("g2-user").type(UserType.GUIDE).build();
        Matching g1Matching = Matching.builder()
                .eventId(1L).guideId("g1-private").viId("vi-private").build();
        EventForm viForm = EventForm.builder()
                .eventId(1L).privateId("vi-private").hopeTeam("A").status(EventFormStatus.APPLIED).build();
        EventForm g2Form = EventForm.builder()
                .eventId(1L).privateId("g2-private").hopeTeam("A").status(EventFormStatus.APPLIED).build();

        when(eventRepository.findById(1L)).thenReturn(Optional.of(Event.builder().id(1L).build()));
        when(userRepository.findUserByUserId("vi-user")).thenReturn(Optional.of(vi));
        when(userRepository.findUserByUserId("g2-user")).thenReturn(Optional.of(g2));
        when(userRepository.findUserByPrivateId("g1-private")).thenReturn(Optional.of(g1));
        // 현재 VI-A ↔ [G1]
        when(matchingRepository.findAllByEventIdAndViId(1L, "vi-private")).thenReturn(List.of(g1Matching));
        when(eventFormRepository.findByEventIdAndPrivateIdAndStatus(1L, "vi-private", EventFormStatus.APPLIED))
                .thenReturn(viForm);
        when(eventFormRepository.findByEventIdAndPrivateIdAndStatus(1L, "g2-private", EventFormStatus.APPLIED))
                .thenReturn(g2Form);
        when(matchingRepository.findByEventIdAndGuideId(1L, "g2-private")).thenReturn(null);
        when(unMatchingRepository.findByPrivateIdAndEventId("vi-private", 1L)).thenReturn(Optional.empty());
        lenient().when(attendanceRepository.findByEventIdAndPrivateId(1L, "g2-private")).thenReturn(null);

        MatchingCreateRequest request = new MatchingCreateRequest("vi-user", List.of("g2-user"));
        eventMatchingService.createMatching(1L, request);

        // G1 은 매칭 해제 + 파트너 원복 + 대기 전환
        verify(matchingRepository).delete(g1Matching);
        verify(partnerService).setNotAttendGuidePartner(1L, g1);
        ArgumentCaptor<UnMatching> unMatchingCaptor = ArgumentCaptor.forClass(UnMatching.class);
        verify(unMatchingRepository).save(unMatchingCaptor.capture());
        assertThat(unMatchingCaptor.getValue().getPrivateId()).isEqualTo("g1-private");
        // G2 는 VI-A 에 매칭 저장
        ArgumentCaptor<Matching> matchingCaptor = ArgumentCaptor.forClass(Matching.class);
        verify(matchingRepository).save(matchingCaptor.capture());
        assertThat(matchingCaptor.getValue().getGuideId()).isEqualTo("g2-private");
        assertThat(matchingCaptor.getValue().getViId()).isEqualTo("vi-private");
    }

    @Test
    @DisplayName("매칭 생성은 빈 guideIds 를 거절한다(최소 1명)")
    void createMatchingRejectsEmptyGuideIds() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(Event.builder().id(1L).build()));

        MatchingCreateRequest request = new MatchingCreateRequest("vi-user", List.of());

        assertThatThrownBy(() -> eventMatchingService.createMatching(1L, request))
                .isInstanceOf(EventValidationException.class);
    }

    private List<MatchingWaitingFlatDto> unsortedWaitingParticipants() {
        return List.of(
                new MatchingWaitingFlatDto(
                        "guide-ha",
                        "하가이드",
                        UserType.GUIDE,
                        "A",
                        "상관없음",
                        "가이드 먼저 들어온 원본 순서입니다.",
                        0,
                        0
                ),
                new MatchingWaitingFlatDto(
                        "vi-choi",
                        "최시각",
                        UserType.VI,
                        "A",
                        "상관없음",
                        "시각 참가자입니다.",
                        0,
                        0
                ),
                new MatchingWaitingFlatDto(
                        "guide-kim",
                        "김가이드",
                        UserType.GUIDE,
                        "A",
                        "상관없음",
                        "가이드 참가자입니다.",
                        0,
                        0
                ),
                new MatchingWaitingFlatDto(
                        "vi-kim",
                        "김시각",
                        UserType.VI,
                        "A",
                        "상관없음",
                        "시각 참가자입니다.",
                        0,
                        0
                )
        );
    }

    private MatchingCompletedFlatDto matchedFlat(
            String viUserId,
            String viName,
            String viGroup,
            String guideUserId,
            String guideName,
            String guideGroup
    ) {
        return new MatchingCompletedFlatDto(
                viUserId,
                UserType.VI,
                viName,
                viGroup,
                false,
                viGroup,
                viGroup,
                guideUserId,
                UserType.GUIDE,
                guideName,
                guideGroup,
                false,
                guideGroup
        );
    }
}
