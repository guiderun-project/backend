package com.guide.run.event.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guide.run.attendance.repository.AttendanceRepository;
import com.guide.run.attendance.service.AttendService;
import com.guide.run.event.entity.Event;
import com.guide.run.event.entity.EventForm;
import com.guide.run.event.entity.dto.request.EventCreateRequest;
import com.guide.run.event.entity.dto.response.EventCreatedResponse;
import com.guide.run.event.entity.dto.response.EventDetailResponse;
import com.guide.run.event.entity.dto.response.EventPopUpResponse;
import com.guide.run.event.entity.dto.response.EventRunningDistancePatchResponse;
import com.guide.run.event.entity.dto.response.EventUpdatedResponse;
import com.guide.run.event.entity.dto.response.MissingRunningDistanceGetResponse;
import com.guide.run.event.entity.type.AdditionalQuestionType;
import com.guide.run.event.entity.type.CityName;
import com.guide.run.event.entity.repository.CommentLikeRepository;
import com.guide.run.event.entity.repository.EventCommentRepository;
import com.guide.run.event.entity.repository.EventFormRepository;
import com.guide.run.event.entity.repository.EventLikeRepository;
import com.guide.run.event.entity.repository.EventRepository;
import com.guide.run.event.entity.type.EventCategory;
import com.guide.run.event.entity.type.EventFormStatus;
import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.event.entity.type.EventStatus;
import com.guide.run.event.entity.type.EventType;
import com.guide.run.global.converter.TimeFormatter;
import com.guide.run.global.exception.event.authorize.NotEventOrganizerException;
import com.guide.run.global.exception.event.logic.CannotModifyAdditionalQuestionsException;
import com.guide.run.global.exception.event.logic.EventValidationException;
import com.guide.run.global.exception.user.authorize.NotAuthorizationException;
import com.guide.run.partner.entity.matching.repository.MatchingRepository;
import com.guide.run.partner.entity.matching.repository.UnMatchingRepository;
import com.guide.run.user.entity.type.UserType;
import com.guide.run.user.entity.user.User;
import com.guide.run.user.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventRenewalServiceTest {

    @Mock
    private EventRepository eventRepository;
    @Mock
    private EventFormRepository eventFormRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MatchingRepository matchingRepository;
    @Mock
    private TimeFormatter timeFormatter;
    @Mock
    private UnMatchingRepository unMatchingRepository;
    @Mock
    private AttendanceRepository attendanceRepository;
    @Mock
    private EventCommentRepository eventCommentRepository;
    @Mock
    private CommentLikeRepository commentLikeRepository;
    @Mock
    private EventLikeRepository eventLikeRepository;
    @Mock
    private AttendService attendService;
    @Mock
    private EventAdditionalInfoService eventAdditionalInfoService;

    @InjectMocks
    private EventService eventService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("이벤트 생성은 비공개 여부, 예상 거리, 추가 질문을 함께 반영한다")
    void eventCreateReflectsRenewalFieldsAndAdditionalQuestions() {
        User organizer = createUser("organizer-private", "organizer-user", "홍길동", UserType.GUIDE);
        List<EventCreateRequest.AdditionalQuestionRequest> additionalQuestions = createAdditionalQuestions();
        EventCreateRequest request = createEventRequest(true, new BigDecimal("7.50"), additionalQuestions);

        when(userRepository.findUserByPrivateId("organizer-private")).thenReturn(Optional.of(organizer));
        when(timeFormatter.getDateTime("2026-06-20", "09:00"))
                .thenReturn(LocalDateTime.of(2026, 6, 20, 9, 0));
        when(timeFormatter.getDateTime("2026-06-20", "11:00"))
                .thenReturn(LocalDateTime.of(2026, 6, 20, 11, 0));
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> {
            Event event = invocation.getArgument(0);
            return Event.builder()
                    .id(99L)
                    .organizer(event.getOrganizer())
                    .recruitStartDate(event.getRecruitStartDate())
                    .recruitEndDate(event.getRecruitEndDate())
                    .name(event.getName())
                    .recruitStatus(event.getRecruitStatus())
                    .isApprove(event.isApprove())
                    .type(event.getType())
                    .startTime(event.getStartTime())
                    .endTime(event.getEndTime())
                    .maxNumV(event.getMaxNumV())
                    .maxNumG(event.getMaxNumG())
                    .place(event.getPlace())
                    .content(event.getContent())
                    .status(event.getStatus())
                    .cityName(event.getCityName())
                    .eventCategory(event.getEventCategory())
                    .isPrivate(event.isPrivate())
                    .expectedRunningDistanceKm(event.getExpectedRunningDistanceKm())
                    .build();
        });

        EventCreatedResponse response = eventService.eventCreate(request, "organizer-private");

        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        verify(eventRepository).save(eventCaptor.capture());
        Event savedEvent = eventCaptor.getValue();
        assertThat(savedEvent.isPrivate()).isTrue();
        assertThat(savedEvent.getExpectedRunningDistanceKm()).isEqualByComparingTo("7.50");
        assertThat(response.getEventId()).isEqualTo(99L);
        ArgumentCaptor<EventForm> formCaptor = ArgumentCaptor.forClass(EventForm.class);
        verify(eventFormRepository).save(formCaptor.capture());
        assertThat(formCaptor.getValue().getRunningDistanceKm()).isEqualByComparingTo("7.50");
        verify(eventAdditionalInfoService).replaceQuestions(99L, additionalQuestions);
    }

    @Test
    @DisplayName("이벤트 수정은 신청자가 없으면 비공개 여부, 예상 거리, 추가 질문을 함께 반영한다")
    void eventUpdateReflectsRenewalFieldsAndAdditionalQuestionsWhenNoAppliedForm() {
        User organizer = createUser("organizer-private", "organizer-user", "홍길동", UserType.GUIDE);
        Event event = createEvent("organizer-private");
        List<EventCreateRequest.AdditionalQuestionRequest> additionalQuestions = createAdditionalQuestions();
        EventCreateRequest request = createEventRequest(false, new BigDecimal("9.20"), additionalQuestions);

        when(userRepository.findUserByPrivateId("organizer-private")).thenReturn(Optional.of(organizer));
        when(timeFormatter.getDateTime("2026-06-20", "09:00"))
                .thenReturn(LocalDateTime.of(2026, 6, 20, 9, 0));
        when(timeFormatter.getDateTime("2026-06-20", "11:00"))
                .thenReturn(LocalDateTime.of(2026, 6, 20, 11, 0));
        when(eventRepository.findById(99L)).thenReturn(Optional.of(event));
        when(eventFormRepository.countByEventIdAndStatus(99L, EventFormStatus.APPLIED)).thenReturn(0L);
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> {
            Event saved = invocation.getArgument(0);
            return Event.builder()
                    .id(saved.getId())
                    .organizer(saved.getOrganizer())
                    .recruitStartDate(saved.getRecruitStartDate())
                    .recruitEndDate(saved.getRecruitEndDate())
                    .name(saved.getName())
                    .recruitStatus(saved.getRecruitStatus())
                    .isApprove(saved.isApprove())
                    .type(saved.getType())
                    .startTime(saved.getStartTime())
                    .endTime(saved.getEndTime())
                    .maxNumV(saved.getMaxNumV())
                    .maxNumG(saved.getMaxNumG())
                    .place(saved.getPlace())
                    .content(saved.getContent())
                    .status(saved.getStatus())
                    .cityName(saved.getCityName())
                    .eventCategory(saved.getEventCategory())
                    .isPrivate(saved.isPrivate())
                    .expectedRunningDistanceKm(saved.getExpectedRunningDistanceKm())
                    .build();
        });

        EventUpdatedResponse response = eventService.eventUpdate(request, "organizer-private", 99L);

        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        verify(eventRepository).save(eventCaptor.capture());
        Event savedEvent = eventCaptor.getValue();
        assertThat(savedEvent.isPrivate()).isFalse();
        assertThat(savedEvent.getExpectedRunningDistanceKm()).isEqualByComparingTo("9.20");
        assertThat(response.getEventId()).isEqualTo(99L);
        verify(eventAdditionalInfoService).replaceQuestions(99L, additionalQuestions);
    }

    @Test
    @DisplayName("이벤트 수정은 신청자가 있으면 추가 질문 변경을 거부한다")
    void eventUpdateRejectsAdditionalQuestionsWhenAppliedFormExists() {
        User organizer = createUser("organizer-private", "organizer-user", "홍길동", UserType.GUIDE);
        Event event = createEvent("organizer-private");
        EventCreateRequest request = createEventRequest(false, new BigDecimal("9.20"), createAdditionalQuestions());

        when(userRepository.findUserByPrivateId("organizer-private")).thenReturn(Optional.of(organizer));
        when(timeFormatter.getDateTime("2026-06-20", "09:00"))
                .thenReturn(LocalDateTime.of(2026, 6, 20, 9, 0));
        when(timeFormatter.getDateTime("2026-06-20", "11:00"))
                .thenReturn(LocalDateTime.of(2026, 6, 20, 11, 0));
        when(eventRepository.findById(99L)).thenReturn(Optional.of(event));
        when(eventFormRepository.countByEventIdAndStatus(99L, EventFormStatus.APPLIED)).thenReturn(1L);

        assertThatThrownBy(() -> eventService.eventUpdate(request, "organizer-private", 99L))
                .isInstanceOf(CannotModifyAdditionalQuestionsException.class)
                .hasMessage("신청자가 있는 이벤트는 추가정보를 수정할 수 없습니다.");
        verify(eventRepository, never()).save(any(Event.class));
        verify(eventAdditionalInfoService, never()).replaceQuestions(any(), any());
    }

    @Test
    @DisplayName("이벤트 삭제는 이벤트 삭제 전에 추가 질문과 답변을 정리한다")
    void eventDeleteRemovesAdditionalInfoBeforeDeletingEvent() {
        User organizer = createUser("organizer-private", "organizer-user", "홍길동", UserType.GUIDE);
        Event event = createEvent("organizer-private");

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(userRepository.findUserByPrivateId("organizer-private")).thenReturn(Optional.of(organizer));
        when(eventCommentRepository.findAllByEventId(1L)).thenReturn(List.of());

        eventService.eventDelete("organizer-private", 1L);

        InOrder inOrder = inOrder(eventAdditionalInfoService, eventRepository);
        inOrder.verify(eventAdditionalInfoService).deleteAllForEvent(1L);
        inOrder.verify(eventRepository).deleteById(1L);
    }

    @Test
    @DisplayName("러닝 거리 미입력 이벤트 조회는 내가 주최한 종료 이벤트 중 최신 1개를 반환한다")
    void getMissingRunningDistanceReturnsLatestOrganizerEndedEvent() {
        Event missingDistanceEvent = createEndedEventWithoutExpectedDistance(10L, "상계천천히달리기");
        when(eventRepository.findAllByOrganizerAndEndTimeBeforeAndExpectedRunningDistanceKmIsNullOrderByEndTimeDescIdDesc(
                eq("organizer-private"),
                any(LocalDateTime.class),
                any(Pageable.class)
        )).thenReturn(List.of(missingDistanceEvent));

        MissingRunningDistanceGetResponse response = eventService.getMissingRunningDistance("organizer-private");

        assertThat(response.getItems()).hasSize(1);
        MissingRunningDistanceGetResponse.Item item = response.getItems().get(0);
        assertThat(item.getEventId()).isEqualTo(10L);
        assertThat(item.getName()).isEqualTo("상계천천히달리기");
        assertThat(item.getDateText()).isEqualTo("6월 1일 (월)");
    }

    @Test
    @DisplayName("러닝 거리 등록은 이벤트 예상 거리와 APPLIED 신청서 거리를 함께 갱신한다")
    void patchRunningDistanceUpdatesEventAndAppliedFormDistances() {
        Event event = createEndedEventWithoutExpectedDistance(1L, "상계천천히달리기");
        EventForm viForm = EventForm.builder()
                .id(55L)
                .eventId(1L)
                .privateId("vi-private")
                .status(EventFormStatus.APPLIED)
                .runningDistanceKm(null)
                .build();
        EventForm guideForm = EventForm.builder()
                .id(56L)
                .eventId(1L)
                .privateId("guide-private")
                .status(EventFormStatus.APPLIED)
                .runningDistanceKm(new BigDecimal("3.00"))
                .build();
        BigDecimal distance = new BigDecimal("8.25");

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventFormRepository.findAllByEventIdAndStatus(1L, EventFormStatus.APPLIED))
                .thenReturn(List.of(viForm, guideForm));

        EventRunningDistancePatchResponse response = eventService.patchRunningDistance(
                1L,
                "organizer-private",
                distance
        );

        assertThat(event.getExpectedRunningDistanceKm()).isEqualByComparingTo("8.25");
        assertThat(viForm.getRunningDistanceKm()).isEqualByComparingTo("8.25");
        assertThat(guideForm.getRunningDistanceKm()).isEqualByComparingTo("8.25");
        assertThat(response.getEventId()).isEqualTo(1L);
        assertThat(response.getExpectedRunningDistanceKm()).isEqualByComparingTo("8.25");
        verify(eventRepository).save(event);
        verify(eventFormRepository).saveAll(List.of(viForm, guideForm));
    }

    @Test
    @DisplayName("러닝 거리 등록은 종료되지 않은 이벤트면 거부한다")
    void patchRunningDistanceRejectsNotEndedEvent() {
        Event event = createEvent("organizer-private");

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThatThrownBy(() -> eventService.patchRunningDistance(
                1L,
                "organizer-private",
                new BigDecimal("8.25")
        )).isInstanceOf(EventValidationException.class)
                .hasMessage("종료된 이벤트만 러닝 거리를 등록할 수 있습니다.");
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    @DisplayName("러닝 거리 등록은 0보다 큰 값만 허용한다")
    void patchRunningDistanceRejectsNonPositiveDistance() {
        Event event = createEndedEventWithoutExpectedDistance(1L, "상계천천히달리기");

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThatThrownBy(() -> eventService.patchRunningDistance(
                1L,
                "organizer-private",
                BigDecimal.ZERO
        )).isInstanceOf(EventValidationException.class)
                .hasMessage("러닝 거리는 0보다 커야 합니다.");
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    @DisplayName("러닝 거리 등록은 주최자가 아니면 거부한다")
    void patchRunningDistanceRejectsNonOrganizer() {
        Event event = createEndedEventWithoutExpectedDistance(1L, "상계천천히달리기");

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThatThrownBy(() -> eventService.patchRunningDistance(
                1L,
                "other-private",
                new BigDecimal("8.25")
        )).isInstanceOf(NotEventOrganizerException.class);
        verify(eventRepository, never()).save(any(Event.class));
        verify(eventFormRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("러닝 거리 스킵은 이벤트 예상 거리와 APPLIED 신청서 거리를 0으로 갱신한다")
    void skipRunningDistanceUpdatesEventAndAppliedFormDistancesToZero() {
        Event event = createEndedEventWithoutExpectedDistance(1L, "상계천천히달리기");
        EventForm form = EventForm.builder()
                .id(55L)
                .eventId(1L)
                .privateId("vi-private")
                .status(EventFormStatus.APPLIED)
                .runningDistanceKm(null)
                .build();

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventFormRepository.findAllByEventIdAndStatus(1L, EventFormStatus.APPLIED))
                .thenReturn(List.of(form));

        EventRunningDistancePatchResponse response = eventService.skipRunningDistance(1L, "organizer-private");

        assertThat(event.getExpectedRunningDistanceKm()).isEqualByComparingTo("0");
        assertThat(form.getRunningDistanceKm()).isEqualByComparingTo("0");
        assertThat(response.getEventId()).isEqualTo(1L);
        assertThat(response.getExpectedRunningDistanceKm()).isEqualByComparingTo("0");
        verify(eventRepository).save(event);
        verify(eventFormRepository).saveAll(List.of(form));
    }

    @Test
    @DisplayName("비회원 공개 이벤트 상세 조회는 viewer 없이 리뉴얼 상세 정보를 반환한다")
    void getDetailEventReturnsPublicRenewalDetailForGuest() {
        Event event = createEvent("organizer-private", false);
        User organizer = createUser("organizer-private", "organizer-user", "홍길동", UserType.GUIDE);
        List<EventDetailResponse.AdditionalQuestion> questions = List.of(
                EventDetailResponse.AdditionalQuestion.builder()
                        .questionId(1L)
                        .question("하고 싶은 말")
                        .build()
        );

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(userRepository.findUserByPrivateId("organizer-private")).thenReturn(Optional.of(organizer));
        when(eventAdditionalInfoService.getQuestions(1L)).thenReturn(questions);

        EventDetailResponse response = eventService.getDetailEvent(1L, null);

        assertThat(response.getEventId()).isEqualTo(1L);
        assertThat(response.getEventType()).isEqualTo(EventType.TRAINING);
        assertThat(response.isPrivate()).isFalse();
        assertThat(response.getRecruitStartDate()).isEqualTo(LocalDate.of(2026, 6, 1));
        assertThat(response.getRecruitEndDate()).isEqualTo(LocalDate.of(2026, 6, 10));
        assertThat(response.getExpectedRunningDistanceKm()).isEqualByComparingTo("7.50");
        assertThat(response.getAdditionalQuestions()).hasSize(1);
        assertThat(response.getViewer()).isNull();
    }

    @Test
    @DisplayName("비회원 비공개 이벤트 상세 조회는 거부한다")
    void getDetailEventRejectsPrivateEventForGuest() {
        Event event = createEvent("organizer-private", true);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThatThrownBy(() -> eventService.getDetailEvent(1L, null))
                .isInstanceOf(NotAuthorizationException.class);
    }

    @Test
    @DisplayName("회원 이벤트 상세 조회는 신청 여부와 주최자 여부를 viewer에 포함한다")
    void getDetailEventReturnsViewerForMember() {
        Event event = createEvent("organizer-private");
        User organizer = createUser("organizer-private", "organizer-user", "홍길동", UserType.GUIDE);
        EventForm appliedForm = EventForm.builder()
                .eventId(1L)
                .privateId("viewer-private")
                .status(EventFormStatus.APPLIED)
                .build();

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(userRepository.findUserByPrivateId("organizer-private")).thenReturn(Optional.of(organizer));
        when(eventFormRepository.findByEventIdAndPrivateIdAndStatus(1L, "viewer-private", EventFormStatus.APPLIED))
                .thenReturn(appliedForm);
        when(eventAdditionalInfoService.getQuestions(1L)).thenReturn(List.of());

        EventDetailResponse response = eventService.getDetailEvent(1L, "viewer-private");

        assertThat(response.getViewer()).isNotNull();
        assertThat(response.getViewer().isApplied()).isTrue();
        assertThat(response.getViewer().isOrganizer()).isFalse();
    }

    @Test
    @DisplayName("이벤트 팝업 신청 여부는 APPLIED 상태 신청서만 기준으로 판단한다")
    void eventPopUpUsesAppliedFormForApplyStatus() {
        User viewer = createUser("viewer-private", "viewer-user", "김철수", UserType.GUIDE);
        User organizer = createUser("organizer-private", "organizer-user", "홍길동", UserType.GUIDE);
        Event event = createEvent("organizer-private");
        EventForm appliedForm = EventForm.builder()
                .eventId(1L)
                .privateId("viewer-private")
                .status(EventFormStatus.APPLIED)
                .build();
        ReflectionTestUtils.setField(event, "updatedAt", LocalDateTime.now());

        when(userRepository.findUserByPrivateId("viewer-private")).thenReturn(Optional.of(viewer));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(userRepository.findUserByPrivateId("organizer-private")).thenReturn(Optional.of(organizer));
        when(timeFormatter.getHHMM(event.getStartTime())).thenReturn("09:00");
        when(timeFormatter.getHHMM(event.getEndTime())).thenReturn("11:00");
        when(eventFormRepository.findByEventIdAndPrivateIdAndStatus(1L, "viewer-private", EventFormStatus.APPLIED))
                .thenReturn(appliedForm);

        EventPopUpResponse response = eventService.eventPopUp(1L, "viewer-private");

        assertThat(response.getIsApply()).isTrue();
        verify(eventFormRepository).findByEventIdAndPrivateIdAndStatus(1L, "viewer-private", EventFormStatus.APPLIED);
        verify(eventFormRepository, never()).findByEventIdAndPrivateId(1L, "viewer-private");
    }

    @Test
    @DisplayName("이벤트 상세 응답 boolean 필드는 is 접두사 JSON 키를 유지한다")
    void eventDetailResponseSerializesBooleanKeysWithIsPrefix() throws Exception {
        EventDetailResponse response = EventDetailResponse.builder()
                .isPrivate(true)
                .viewer(EventDetailResponse.Viewer.builder()
                        .isApplied(true)
                        .isOrganizer(false)
                        .build())
                .build();

        JsonNode json = objectMapper.readTree(objectMapper.writeValueAsString(response));

        assertThat(json.has("isPrivate")).isTrue();
        assertThat(json.has("private")).isFalse();
        assertThat(json.get("viewer").has("isApplied")).isTrue();
        assertThat(json.get("viewer").has("applied")).isFalse();
        assertThat(json.get("viewer").has("isOrganizer")).isTrue();
        assertThat(json.get("viewer").has("organizer")).isFalse();
    }

    private Event createEvent(String organizer) {
        return createEvent(organizer, true);
    }

    private Event createEvent(String organizer, boolean isPrivate) {
        LocalDateTime eventDate = LocalDateTime.now().plusDays(3).withHour(9).withMinute(0).withSecond(0).withNano(0);
        return Event.builder()
                .id(1L)
                .organizer(organizer)
                .recruitStartDate(LocalDate.of(2026, 6, 1))
                .recruitEndDate(LocalDate.of(2026, 6, 10))
                .name("상계천천히달리기")
                .recruitStatus(EventRecruitStatus.RECRUIT_OPEN)
                .isPrivate(isPrivate)
                .isApprove(true)
                .type(EventType.TRAINING)
                .startTime(eventDate)
                .endTime(eventDate.withHour(11))
                .maxNumV(4)
                .maxNumG(4)
                .place("서울")
                .content("내용")
                .status(EventStatus.EVENT_UPCOMING)
                .eventCategory(EventCategory.GENERAL)
                .expectedRunningDistanceKm(new BigDecimal("7.50"))
                .build();
    }

    private Event createEndedEventWithoutExpectedDistance(Long eventId, String name) {
        return Event.builder()
                .id(eventId)
                .organizer("organizer-private")
                .name(name)
                .startTime(LocalDateTime.of(2026, 6, 1, 9, 0))
                .endTime(LocalDateTime.of(2026, 6, 1, 11, 0))
                .expectedRunningDistanceKm(null)
                .build();
    }

    private EventCreateRequest createEventRequest(
            Boolean isPrivate,
            BigDecimal expectedRunningDistanceKm,
            List<EventCreateRequest.AdditionalQuestionRequest> additionalQuestions
    ) {
        return new EventCreateRequest(
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 10),
                "상계천천히달리기",
                EventType.TRAINING,
                "2026-06-20",
                "09:00",
                "11:00",
                4,
                2,
                "서울",
                "내용",
                EventCategory.GENERAL,
                CityName.SEOUL,
                isPrivate,
                expectedRunningDistanceKm,
                additionalQuestions
        );
    }

    private List<EventCreateRequest.AdditionalQuestionRequest> createAdditionalQuestions() {
        return List.of(
                new EventCreateRequest.AdditionalQuestionRequest(
                        AdditionalQuestionType.TEXT,
                        "하고 싶은 말",
                        List.of()
                ),
                new EventCreateRequest.AdditionalQuestionRequest(
                        AdditionalQuestionType.SELECT,
                        "티셔츠 사이즈",
                        List.of("S", "M", "L")
                )
        );
    }

    private User createUser(String privateId, String userId, String name, UserType type) {
        return User.builder()
                .privateId(privateId)
                .userId(userId)
                .name(name)
                .type(type)
                .build();
    }
}
