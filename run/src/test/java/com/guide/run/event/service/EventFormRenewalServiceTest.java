package com.guide.run.event.service;

import com.guide.run.attendance.repository.AttendanceRepository;
import com.guide.run.attendance.entity.Attendance;
import com.guide.run.event.entity.Event;
import com.guide.run.event.entity.EventForm;
import com.guide.run.event.entity.dto.request.EventApplyRequest;
import com.guide.run.event.entity.dto.response.form.EventApplicantFormResponse;
import com.guide.run.event.entity.dto.response.form.EventApplicantListResponse;
import com.guide.run.event.entity.dto.response.form.EventCanceledApplicantListResponse;
import com.guide.run.event.entity.dto.response.form.MyEventApplyGetResponse;
import com.guide.run.event.entity.repository.EventFormRepository;
import com.guide.run.event.entity.repository.EventRepository;
import com.guide.run.event.entity.type.AdditionalQuestionType;
import com.guide.run.event.entity.type.EventCategory;
import com.guide.run.event.entity.type.EventFormStatus;
import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.event.entity.type.EventType;
import com.guide.run.global.exception.event.authorize.NotEventOrganizerException;
import com.guide.run.global.exception.event.logic.EventValidationException;
import com.guide.run.global.exception.event.logic.NotValidDurationException;
import com.guide.run.partner.entity.matching.UnMatching;
import com.guide.run.partner.entity.matching.repository.MatchingRepository;
import com.guide.run.partner.entity.matching.repository.UnMatchingRepository;
import com.guide.run.user.entity.type.Role;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventFormRenewalServiceTest {
    @Mock
    private EventFormRepository eventFormRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private AttendanceRepository attendanceRepository;
    @Mock
    private UnMatchingRepository unMatchingRepository;
    @Mock
    private MatchingRepository matchingRepository;
    @Mock
    private EventAdditionalInfoService eventAdditionalInfoService;

    @InjectMocks
    private EventFormService eventFormService;

    @Test
    @DisplayName("대회 신청서 생성은 대회 정보와 추가답변을 함께 저장한다")
    void createCompetitionFormStoresCompetitionInfoAndAdditionalAnswers() {
        Event event = createEvent(EventType.COMPETITION, new BigDecimal("7.50"));
        User user = createUser("user-private", UserType.VI);
        EventApplyRequest request = createApplyRequest();

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(userRepository.findUserByPrivateId("user-private")).thenReturn(Optional.of(user));
        lenient().when(eventFormRepository.findAllByEventIdAndPrivateId(1L, "user-private")).thenReturn(List.of());
        when(eventFormRepository.findByEventIdAndPrivateIdAndStatus(1L, "user-private", EventFormStatus.APPLIED))
                .thenReturn(null);
        when(eventFormRepository.save(any(EventForm.class))).thenAnswer(invocation -> {
            EventForm form = invocation.getArgument(0);
            return EventForm.builder()
                    .id(55L)
                    .privateId(form.getPrivateId())
                    .eventId(form.getEventId())
                    .type(form.getType())
                    .age(form.getAge())
                    .gender(form.getGender())
                    .hopePartner(form.getHopePartner())
                    .hopeTeam(form.getHopeTeam())
                    .referContent(form.getReferContent())
                    .isMatching(form.isMatching())
                    .eventCategory(form.getEventCategory())
                    .runningDistanceKm(form.getRunningDistanceKm())
                    .birthDate(form.getBirthDate())
                    .phoneNumber(form.getPhoneNumber())
                    .status(form.getStatus())
                    .build();
        });

        Long formId = eventFormService.createForm(request, 1L, "user-private");

        ArgumentCaptor<EventForm> formCaptor = ArgumentCaptor.forClass(EventForm.class);
        verify(eventFormRepository).save(formCaptor.capture());
        EventForm savedForm = formCaptor.getValue();
        assertThat(savedForm.getStatus()).isEqualTo(EventFormStatus.APPLIED);
        assertThat(savedForm.getHopeTeam()).isEqualTo("A");
        assertThat(savedForm.getHopePartner()).isEqualTo("김가이드");
        assertThat(savedForm.getReferContent()).isEqualTo("대회 참가");
        assertThat(savedForm.getRunningDistanceKm()).isEqualByComparingTo("7.50");
        assertThat(savedForm.getBirthDate()).isEqualTo(LocalDate.of(1990, 1, 2));
        assertThat(savedForm.getPhoneNumber()).isEqualTo("010-1234-5678");
        assertThat(formId).isEqualTo(55L);
        verify(eventAdditionalInfoService).replaceAnswers(1L, 55L, request.getAdditionalAnswers());
    }

    @Test
    @DisplayName("신청서 생성은 CANCELED 이력이 있어도 APPLIED 신청서가 없으면 허용한다")
    void createFormAllowsCanceledHistoryWhenNoAppliedFormExists() {
        Event event = createEvent(EventType.TRAINING);
        User user = createUser("user-private", UserType.VI);
        EventApplyRequest request = new EventApplyRequest("A", "김가이드", "훈련 참가", null, List.of());
        EventForm canceledForm = EventForm.builder()
                .id(44L)
                .eventId(1L)
                .privateId("user-private")
                .status(EventFormStatus.CANCELED)
                .build();

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(userRepository.findUserByPrivateId("user-private")).thenReturn(Optional.of(user));
        lenient().when(eventFormRepository.findAllByEventIdAndPrivateId(1L, "user-private"))
                .thenReturn(List.of(canceledForm));
        when(eventFormRepository.findByEventIdAndPrivateIdAndStatus(1L, "user-private", EventFormStatus.APPLIED))
                .thenReturn(null);
        when(eventFormRepository.save(any(EventForm.class))).thenReturn(EventForm.builder()
                .id(55L)
                .eventId(1L)
                .privateId("user-private")
                .status(EventFormStatus.APPLIED)
                .build());

        Long formId = eventFormService.createForm(request, 1L, "user-private");

        ArgumentCaptor<EventForm> formCaptor = ArgumentCaptor.forClass(EventForm.class);
        verify(eventFormRepository).save(formCaptor.capture());
        assertThat(formCaptor.getValue().getRunningDistanceKm()).isNull();
        assertThat(formId).isEqualTo(55L);
        verify(eventAdditionalInfoService).replaceAnswers(1L, 55L, request.getAdditionalAnswers());
    }

    @Test
    @DisplayName("신청서 생성은 저장된 상태가 모집중이어도 이벤트 시작 시간이 지났으면 거절한다")
    void createFormRejectsStaleOpenStatusAfterEventStart() {
        LocalDate today = EventTemporalStatusResolver.today();
        LocalDateTime now = EventTemporalStatusResolver.now();
        Event event = createEvent(
                EventType.TRAINING,
                EventRecruitStatus.RECRUIT_OPEN,
                today.minusDays(2),
                today.plusDays(2),
                now.minusMinutes(1),
                now.plusHours(1)
        );

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThatThrownBy(() -> eventFormService.createForm(
                new EventApplyRequest("A", "김가이드", "훈련 참가", null, List.of()),
                1L,
                "user-private"
        )).isInstanceOf(NotValidDurationException.class);

        verify(userRepository, never()).findUserByPrivateId("user-private");
    }

    @Test
    @DisplayName("신청서 생성은 저장된 상태가 모집예정이어도 모집 시간이 열려 있으면 허용한다")
    void createFormAllowsStaleUpcomingStatusDuringRecruitPeriod() {
        LocalDate today = EventTemporalStatusResolver.today();
        LocalDateTime now = EventTemporalStatusResolver.now();
        Event event = createEvent(
                EventType.TRAINING,
                EventRecruitStatus.RECRUIT_UPCOMING,
                today.minusDays(1),
                today.plusDays(1),
                now.plusDays(2),
                now.plusDays(2).plusHours(1)
        );
        User user = createUser("user-private", UserType.VI);
        EventApplyRequest request = new EventApplyRequest("A", "김가이드", "훈련 참가", null, List.of());

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(userRepository.findUserByPrivateId("user-private")).thenReturn(Optional.of(user));
        when(eventFormRepository.findByEventIdAndPrivateIdAndStatus(1L, "user-private", EventFormStatus.APPLIED))
                .thenReturn(null);
        when(eventFormRepository.save(any(EventForm.class))).thenReturn(EventForm.builder()
                .id(55L)
                .eventId(1L)
                .privateId("user-private")
                .status(EventFormStatus.APPLIED)
                .build());

        Long formId = eventFormService.createForm(request, 1L, "user-private");

        assertThat(formId).isEqualTo(55L);
        verify(eventAdditionalInfoService).replaceAnswers(1L, 55L, request.getAdditionalAnswers());
    }

    @Test
    @DisplayName("대회 신청서 생성은 대회 정보가 없으면 거부한다")
    void createCompetitionFormRejectsMissingCompetitionInfo() {
        Event event = createEvent(EventType.COMPETITION);
        User user = createUser("user-private", UserType.VI);
        EventApplyRequest request = new EventApplyRequest(
                "A",
                "김가이드",
                "대회 참가",
                null,
                List.of()
        );

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(userRepository.findUserByPrivateId("user-private")).thenReturn(Optional.of(user));
        lenient().when(eventFormRepository.findAllByEventIdAndPrivateId(1L, "user-private")).thenReturn(List.of());
        when(eventFormRepository.findByEventIdAndPrivateIdAndStatus(1L, "user-private", EventFormStatus.APPLIED))
                .thenReturn(null);

        assertThatThrownBy(() -> eventFormService.createForm(request, 1L, "user-private"))
                .isInstanceOf(EventValidationException.class)
                .hasMessage("대회 신청 정보는 필수입니다.");
        verify(eventFormRepository, never()).save(any(EventForm.class));
        verify(eventAdditionalInfoService, never()).replaceAnswers(any(), any(), any());
    }

    @Test
    @DisplayName("신청서 수정은 APPLIED 신청서만 수정하고 추가답변을 교체한다")
    void patchFormUpdatesAppliedFormAndAdditionalAnswers() {
        Event event = createEvent(EventType.COMPETITION);
        User user = createUser("user-private", UserType.VI);
        EventForm form = EventForm.builder()
                .id(55L)
                .privateId("user-private")
                .eventId(1L)
                .status(EventFormStatus.APPLIED)
                .build();
        EventApplyRequest request = createApplyRequest();

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(userRepository.findUserByPrivateId("user-private")).thenReturn(Optional.of(user));
        when(eventFormRepository.findByEventIdAndPrivateIdAndStatus(1L, "user-private", EventFormStatus.APPLIED))
                .thenReturn(form);
        when(eventFormRepository.save(form)).thenReturn(form);

        Long formId = eventFormService.patchForm(request, 1L, "user-private");

        assertThat(form.getHopeTeam()).isEqualTo("A");
        assertThat(form.getHopePartner()).isEqualTo("김가이드");
        assertThat(form.getReferContent()).isEqualTo("대회 참가");
        assertThat(form.getBirthDate()).isEqualTo(LocalDate.of(1990, 1, 2));
        assertThat(form.getPhoneNumber()).isEqualTo("010-1234-5678");
        assertThat(formId).isEqualTo(55L);
        verify(eventAdditionalInfoService).replaceAnswers(1L, 55L, request.getAdditionalAnswers());
    }

    @Test
    @DisplayName("신청서 수정은 저장된 상태가 모집중이어도 이벤트 시작 시간이 지났으면 거절한다")
    void patchFormRejectsStaleOpenStatusAfterEventStart() {
        LocalDate today = EventTemporalStatusResolver.today();
        LocalDateTime now = EventTemporalStatusResolver.now();
        Event event = createEvent(
                EventType.TRAINING,
                EventRecruitStatus.RECRUIT_OPEN,
                today.minusDays(2),
                today.plusDays(2),
                now.minusMinutes(1),
                now.plusHours(1)
        );

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThatThrownBy(() -> eventFormService.patchForm(
                new EventApplyRequest("A", "김가이드", "훈련 참가", null, List.of()),
                1L,
                "user-private"
        )).isInstanceOf(NotValidDurationException.class);

        verify(userRepository, never()).findUserByPrivateId("user-private");
    }

    @Test
    @DisplayName("신청서 수정은 저장된 상태가 모집예정이어도 모집 시간이 열려 있으면 허용한다")
    void patchFormAllowsStaleUpcomingStatusDuringRecruitPeriod() {
        LocalDate today = EventTemporalStatusResolver.today();
        LocalDateTime now = EventTemporalStatusResolver.now();
        Event event = createEvent(
                EventType.TRAINING,
                EventRecruitStatus.RECRUIT_UPCOMING,
                today.minusDays(1),
                today.plusDays(1),
                now.plusDays(2),
                now.plusDays(2).plusHours(1)
        );
        User user = createUser("user-private", UserType.VI);
        EventForm form = EventForm.builder()
                .id(55L)
                .eventId(1L)
                .privateId("user-private")
                .status(EventFormStatus.APPLIED)
                .build();
        EventApplyRequest request = new EventApplyRequest("A", "김가이드", "훈련 참가", null, List.of());

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(userRepository.findUserByPrivateId("user-private")).thenReturn(Optional.of(user));
        when(eventFormRepository.findByEventIdAndPrivateIdAndStatus(1L, "user-private", EventFormStatus.APPLIED))
                .thenReturn(form);
        when(eventFormRepository.save(form)).thenReturn(form);

        Long formId = eventFormService.patchForm(request, 1L, "user-private");

        assertThat(formId).isEqualTo(55L);
        verify(eventAdditionalInfoService).replaceAnswers(1L, 55L, request.getAdditionalAnswers());
    }

    @Test
    @DisplayName("내 신청서 조회는 이벤트, 사용자, 신청 정보와 추가답변을 반환한다")
    void getMyFormReturnsApplicationDetail() {
        Event event = createEvent(EventType.COMPETITION);
        User user = User.builder()
                .privateId("user-private")
                .userId("user-id")
                .name("홍길동")
                .type(UserType.VI)
                .recordDegree("6:00")
                .build();
        EventForm form = EventForm.builder()
                .id(55L)
                .privateId("user-private")
                .eventId(1L)
                .hopeTeam("A")
                .hopePartner("김가이드")
                .referContent("대회 참가")
                .birthDate(LocalDate.of(1990, 1, 2))
                .phoneNumber("010-1234-5678")
                .status(EventFormStatus.APPLIED)
                .build();
        List<MyEventApplyGetResponse.AdditionalAnswerDetail> answers = List.of(
                MyEventApplyGetResponse.AdditionalAnswerDetail.builder()
                        .questionId(10L)
                        .type(AdditionalQuestionType.TEXT)
                        .question("하고 싶은 말")
                        .answerText("답변")
                        .build()
        );

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(userRepository.findUserByPrivateId("user-private")).thenReturn(Optional.of(user));
        when(eventFormRepository.findByEventIdAndPrivateIdAndStatus(1L, "user-private", EventFormStatus.APPLIED))
                .thenReturn(form);
        when(eventAdditionalInfoService.getAnswerDetails(55L)).thenReturn(answers);

        MyEventApplyGetResponse response = eventFormService.getMyForm(1L, "user-private");

        assertThat(response.getEventId()).isEqualTo(1L);
        assertThat(response.getEventType()).isEqualTo(EventType.COMPETITION);
        assertThat(response.getUserType()).isEqualTo(UserType.VI);
        assertThat(response.getName()).isEqualTo("홍길동");
        assertThat(response.getApplicationInfo().getGroup()).isEqualTo("A");
        assertThat(response.getCompetitionInfo().getBirthDate()).isEqualTo(LocalDate.of(1990, 1, 2));
        assertThat(response.getCompetitionInfo().getPhoneNumber()).isEqualTo("010-1234-5678");
        assertThat(response.getAdditionalAnswers()).hasSize(1);
    }

    @Test
    @DisplayName("신청 취소는 신청서를 삭제하지 않고 CANCELED 상태로 변경한다")
    void deleteFormCancelsAppliedFormWithoutDeletingIt() {
        Event event = createEvent(EventType.TRAINING);
        User user = createUser("user-private", UserType.VI);
        EventForm form = EventForm.builder()
                .id(55L)
                .privateId("user-private")
                .eventId(1L)
                .status(EventFormStatus.APPLIED)
                .build();
        Attendance attendance = Attendance.builder()
                .eventId(1L)
                .privateId("user-private")
                .isAttend(false)
                .build();
        UnMatching unMatching = UnMatching.builder()
                .eventId(1L)
                .privateId("user-private")
                .build();

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(userRepository.findUserByPrivateId("user-private")).thenReturn(Optional.of(user));
        when(eventFormRepository.findByEventIdAndPrivateIdAndStatus(1L, "user-private", EventFormStatus.APPLIED))
                .thenReturn(form);
        when(attendanceRepository.findByEventIdAndPrivateId(1L, "user-private")).thenReturn(attendance);
        when(unMatchingRepository.findByPrivateIdAndEventId("user-private", 1L)).thenReturn(Optional.of(unMatching));

        eventFormService.deleteForm(1L, "user-private");

        assertThat(form.getStatus()).isEqualTo(EventFormStatus.CANCELED);
        assertThat(form.getCanceledAt()).isNotNull();
        verify(eventFormRepository).save(form);
        verify(eventFormRepository, never()).delete(any(EventForm.class));
        verify(attendanceRepository).delete(attendance);
        verify(unMatchingRepository).delete(unMatching);
    }

    @Test
    @DisplayName("신청자 명단은 APPLIED 신청서만 그룹화해서 반환한다")
    void getApplicantFormsReturnsAppliedFormsGroupedByRunningGroup() {
        EventForm viForm = EventForm.builder()
                .id(55L)
                .privateId("vi-private")
                .eventId(1L)
                .type(UserType.VI)
                .hopeTeam("A")
                .status(EventFormStatus.APPLIED)
                .build();
        EventForm guideForm = EventForm.builder()
                .id(56L)
                .privateId("guide-private")
                .eventId(1L)
                .type(UserType.GUIDE)
                .hopeTeam("A")
                .status(EventFormStatus.APPLIED)
                .build();
        User vi = User.builder()
                .privateId("vi-private")
                .userId("vi-user")
                .name("VI 사용자")
                .type(UserType.VI)
                .trainingCnt(0)
                .competitionCnt(0)
                .build();
        User guide = User.builder()
                .privateId("guide-private")
                .userId("guide-user")
                .name("가이드 사용자")
                .type(UserType.GUIDE)
                .trainingCnt(1)
                .competitionCnt(0)
                .build();

        when(eventFormRepository.findAllByEventIdAndStatus(1L, EventFormStatus.APPLIED))
                .thenReturn(List.of(viForm, guideForm));
        when(userRepository.findUserByPrivateId("vi-private")).thenReturn(Optional.of(vi));
        when(userRepository.findUserByPrivateId("guide-private")).thenReturn(Optional.of(guide));

        EventApplicantListResponse response = eventFormService.getApplicantForms(1L);

        assertThat(response.getSummary().getTotalCount()).isEqualTo(2);
        assertThat(response.getSummary().getViCount()).isEqualTo(1);
        assertThat(response.getSummary().getGuideCount()).isEqualTo(1);
        assertThat(response.getGroups()).hasSize(1);
        EventApplicantListResponse.EventApplicantGroup group = response.getGroups().get(0);
        assertThat(group.getRunningGroup()).isEqualTo("A");
        assertThat(group.getTotalCount()).isEqualTo(2);
        assertThat(group.getApplicants()).extracting(EventApplicantListResponse.EventApplicant::getUserId)
                .containsExactly("vi-user", "guide-user");
        assertThat(group.getApplicants().get(0).isFirstParticipation()).isTrue();
        assertThat(group.getApplicants().get(1).isFirstParticipation()).isFalse();
    }

    @Test
    @DisplayName("신청자 명단은 러닝그룹 A-E 순서와 그룹 내 VI, Guide 이름순으로 정렬한다")
    void getApplicantFormsSortsGroupsAndApplicants() {
        EventForm guideB = applicantForm("guide-b-private", UserType.GUIDE, "B");
        EventForm viC = applicantForm("vi-c-private", UserType.VI, "C");
        EventForm guideAHa = applicantForm("guide-a-ha-private", UserType.GUIDE, "A");
        EventForm viANa = applicantForm("vi-a-na-private", UserType.VI, "A");
        EventForm guideAGa = applicantForm("guide-a-ga-private", UserType.GUIDE, "A");
        EventForm viAGa = applicantForm("vi-a-ga-private", UserType.VI, "A");

        when(eventFormRepository.findAllByEventIdAndStatus(1L, EventFormStatus.APPLIED))
                .thenReturn(List.of(guideB, viC, guideAHa, viANa, guideAGa, viAGa));
        when(userRepository.findUserByPrivateId("guide-b-private"))
                .thenReturn(Optional.of(applicantUser("guide-b", "나가이드", UserType.GUIDE)));
        when(userRepository.findUserByPrivateId("vi-c-private"))
                .thenReturn(Optional.of(applicantUser("vi-c", "다비", UserType.VI)));
        when(userRepository.findUserByPrivateId("guide-a-ha-private"))
                .thenReturn(Optional.of(applicantUser("guide-a-ha", "하가이드", UserType.GUIDE)));
        when(userRepository.findUserByPrivateId("vi-a-na-private"))
                .thenReturn(Optional.of(applicantUser("vi-a-na", "나비", UserType.VI)));
        when(userRepository.findUserByPrivateId("guide-a-ga-private"))
                .thenReturn(Optional.of(applicantUser("guide-a-ga", "가가이드", UserType.GUIDE)));
        when(userRepository.findUserByPrivateId("vi-a-ga-private"))
                .thenReturn(Optional.of(applicantUser("vi-a-ga", "가비", UserType.VI)));

        EventApplicantListResponse response = eventFormService.getApplicantForms(1L);

        assertThat(response.getGroups())
                .extracting(EventApplicantListResponse.EventApplicantGroup::getRunningGroup)
                .containsExactly("A", "B", "C");
        assertThat(response.getGroups().get(0).getApplicants())
                .extracting(EventApplicantListResponse.EventApplicant::getUserId)
                .containsExactly("vi-a-ga", "vi-a-na", "guide-a-ga", "guide-a-ha");
    }

    @Test
    @DisplayName("취소자 명단은 재신청해서 APPLIED 상태인 참가자를 제외한다")
    void getCanceledApplicantFormsExcludesReappliedUsers() {
        Event event = createEvent(EventType.TRAINING);
        EventForm canceledOnlyForm = EventForm.builder()
                .id(55L)
                .privateId("canceled-only-private")
                .eventId(1L)
                .status(EventFormStatus.CANCELED)
                .canceledAt(LocalDateTime.of(2026, 1, 1, 10, 0))
                .build();
        EventForm reappliedCanceledForm = EventForm.builder()
                .id(56L)
                .privateId("reapplied-private")
                .eventId(1L)
                .status(EventFormStatus.CANCELED)
                .canceledAt(LocalDateTime.of(2026, 1, 1, 11, 0))
                .build();
        EventForm reappliedActiveForm = EventForm.builder()
                .id(57L)
                .privateId("reapplied-private")
                .eventId(1L)
                .status(EventFormStatus.APPLIED)
                .build();
        User canceledOnlyUser = User.builder()
                .privateId("canceled-only-private")
                .userId("canceled-only-user")
                .name("취소만한 사용자")
                .type(UserType.VI)
                .build();
        User reappliedUser = User.builder()
                .privateId("reapplied-private")
                .userId("reapplied-user")
                .name("재신청 사용자")
                .type(UserType.GUIDE)
                .build();

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventFormRepository.findAllByEventIdAndStatus(1L, EventFormStatus.CANCELED))
                .thenReturn(List.of(canceledOnlyForm, reappliedCanceledForm));
        lenient().when(eventFormRepository.findAllByEventIdAndStatus(1L, EventFormStatus.APPLIED))
                .thenReturn(List.of(reappliedActiveForm));
        when(userRepository.findUserByPrivateId("canceled-only-private")).thenReturn(Optional.of(canceledOnlyUser));
        lenient().when(userRepository.findUserByPrivateId("reapplied-private")).thenReturn(Optional.of(reappliedUser));

        EventCanceledApplicantListResponse response = eventFormService.getCanceledApplicantForms(1L);

        assertThat(response.getSummary().getTotalCount()).isEqualTo(1);
        assertThat(response.getSummary().getViCount()).isEqualTo(1);
        assertThat(response.getSummary().getGuideCount()).isZero();
        assertThat(response.getCanceledApplicants())
                .extracting(EventCanceledApplicantListResponse.CanceledApplicant::getUserId)
                .containsExactly("canceled-only-user");
    }

    @Test
    @DisplayName("신청자 신청서 상세는 이벤트 주최자가 조회할 수 있다")
    void getApplicantFormAllowsOrganizer() {
        Event event = Event.builder()
                .id(1L)
                .organizer("organizer-private")
                .build();
        User requester = User.builder()
                .privateId("organizer-private")
                .role(Role.ROLE_USER)
                .build();
        User applicant = User.builder()
                .privateId("applicant-private")
                .userId("applicant-user")
                .name("신청자")
                .type(UserType.VI)
                .build();
        EventForm form = EventForm.builder()
                .id(55L)
                .privateId("applicant-private")
                .eventId(1L)
                .hopeTeam("A")
                .hopePartner("김가이드")
                .referContent("대회 참가")
                .birthDate(LocalDate.of(1990, 1, 2))
                .phoneNumber("010-1234-5678")
                .status(EventFormStatus.APPLIED)
                .build();
        List<MyEventApplyGetResponse.AdditionalAnswerDetail> answers = List.of(
                MyEventApplyGetResponse.AdditionalAnswerDetail.builder()
                        .questionId(10L)
                        .type(AdditionalQuestionType.TEXT)
                        .question("하고 싶은 말")
                        .answerText("답변")
                        .build()
        );

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(userRepository.findUserByPrivateId("organizer-private")).thenReturn(Optional.of(requester));
        when(userRepository.findUserByUserId("applicant-user")).thenReturn(Optional.of(applicant));
        when(eventFormRepository.findByEventIdAndPrivateIdAndStatus(1L, "applicant-private", EventFormStatus.APPLIED))
                .thenReturn(form);
        when(eventAdditionalInfoService.getAnswerDetails(55L)).thenReturn(answers);

        EventApplicantFormResponse response = eventFormService.getApplicantForm(
                1L,
                "applicant-user",
                "organizer-private"
        );

        assertThat(response.getApplicant().getUserId()).isEqualTo("applicant-user");
        assertThat(response.getApplicant().getBirthDate()).isEqualTo(LocalDate.of(1990, 1, 2));
        assertThat(response.getApplicant().getPhoneNumber()).isEqualTo("010-1234-5678");
        assertThat(response.getForm().getApplyGroup()).isEqualTo("A");
        assertThat(response.getAdditionalAnswers()).hasSize(1);
        assertThat(response.getAdditionalAnswers().get(0).getAnswer()).isEqualTo("답변");
    }

    @Test
    @DisplayName("신청자 신청서 상세는 주최자나 관리자가 아니면 거부한다")
    void getApplicantFormRejectsNonOrganizer() {
        Event event = Event.builder()
                .id(1L)
                .organizer("organizer-private")
                .build();
        User requester = User.builder()
                .privateId("viewer-private")
                .role(Role.ROLE_USER)
                .build();

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(userRepository.findUserByPrivateId("viewer-private")).thenReturn(Optional.of(requester));

        assertThatThrownBy(() -> eventFormService.getApplicantForm(
                1L,
                "applicant-user",
                "viewer-private"
        )).isInstanceOf(NotEventOrganizerException.class);
    }

    @Test
    @DisplayName("기존 전체 신청자 상세 조회는 APPLIED 신청서만 조회한다")
    void getAllFormsUsesAppliedStatusFilters() {
        User admin = User.builder()
                .privateId("admin-private")
                .role(Role.ROLE_ADMIN)
                .build();

        when(userRepository.findUserByPrivateId("admin-private")).thenReturn(Optional.of(admin));
        when(eventFormRepository.findAllFormsWithPhone(1L, UserType.VI, EventFormStatus.APPLIED))
                .thenReturn(List.of());
        when(eventFormRepository.findAllFormsWithPhone(1L, UserType.GUIDE, EventFormStatus.APPLIED))
                .thenReturn(List.of());

        eventFormService.getAllForms(1L, "admin-private");

        verify(eventFormRepository).findAllFormsWithPhone(1L, UserType.VI, EventFormStatus.APPLIED);
        verify(eventFormRepository).findAllFormsWithPhone(1L, UserType.GUIDE, EventFormStatus.APPLIED);
    }

    private Event createEvent(EventType eventType) {
        return createEvent(eventType, null);
    }

    private Event createEvent(EventType eventType, BigDecimal expectedRunningDistanceKm) {
        return Event.builder()
                .id(1L)
                .name("상계천천히달리기")
                .type(eventType)
                .recruitStatus(EventRecruitStatus.RECRUIT_OPEN)
                .eventCategory(EventCategory.GENERAL)
                .expectedRunningDistanceKm(expectedRunningDistanceKm)
                .build();
    }

    private Event createEvent(EventType eventType,
                              EventRecruitStatus recruitStatus,
                              LocalDate recruitStartDate,
                              LocalDate recruitEndDate,
                              LocalDateTime startTime,
                              LocalDateTime endTime) {
        return Event.builder()
                .id(1L)
                .name("상계천천히달리기")
                .type(eventType)
                .recruitStatus(recruitStatus)
                .recruitStartDate(recruitStartDate)
                .recruitEndDate(recruitEndDate)
                .startTime(startTime)
                .endTime(endTime)
                .eventCategory(EventCategory.GENERAL)
                .build();
    }

    private User createUser(String privateId, UserType type) {
        return User.builder()
                .privateId(privateId)
                .type(type)
                .age(30)
                .gender("M")
                .build();
    }

    private EventForm applicantForm(String privateId, UserType type, String hopeTeam) {
        return EventForm.builder()
                .privateId(privateId)
                .eventId(1L)
                .type(type)
                .hopeTeam(hopeTeam)
                .status(EventFormStatus.APPLIED)
                .build();
    }

    private User applicantUser(String userId, String name, UserType type) {
        return User.builder()
                .privateId(userId + "-private")
                .userId(userId)
                .name(name)
                .type(type)
                .trainingCnt(1)
                .competitionCnt(0)
                .build();
    }

    private EventApplyRequest createApplyRequest() {
        return new EventApplyRequest(
                "A",
                "김가이드",
                "대회 참가",
                new EventApplyRequest.CompetitionApplicationInfo(
                        LocalDate.of(1990, 1, 2),
                        "010-1234-5678"
                ),
                List.of(new EventApplyRequest.AdditionalAnswerRequest(
                        10L,
                        AdditionalQuestionType.TEXT,
                        "답변",
                        null
                ))
        );
    }
}
