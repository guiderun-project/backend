package com.guide.run.event.service;

import com.guide.run.attendance.repository.AttendanceRepository;
import com.guide.run.attendance.service.AttendService;
import com.guide.run.event.entity.Event;
import com.guide.run.event.entity.EventForm;
import com.guide.run.event.entity.dto.request.EventCreateRequest;
import com.guide.run.event.entity.dto.response.EventCreatedResponse;
import com.guide.run.event.entity.dto.response.EventDetailResponse;
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
import com.guide.run.partner.entity.matching.repository.MatchingRepository;
import com.guide.run.partner.entity.matching.repository.UnMatchingRepository;
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
import static org.mockito.ArgumentMatchers.any;
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

    @Test
    @DisplayName("이벤트 생성은 비공개 여부, 예상 거리, 추가 질문을 함께 반영한다")
    void eventCreateReflectsRenewalFieldsAndAdditionalQuestions() {
        User organizer = createUser("organizer-private", "organizer-user", "홍길동", UserType.GUIDE);
        List<EventCreateRequest.AdditionalQuestionRequest> additionalQuestions = List.of(
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
        EventCreateRequest request = new EventCreateRequest(
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
                true,
                new BigDecimal("7.50"),
                additionalQuestions
        );

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
        verify(eventAdditionalInfoService).replaceQuestions(99L, additionalQuestions);
    }

    @Test
    @DisplayName("비회원 이벤트 상세 조회는 viewer 없이 리뉴얼 상세 정보를 반환한다")
    void getDetailEventReturnsPublicRenewalDetailForGuest() {
        Event event = createEvent("organizer-private");
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
        assertThat(response.isPrivate()).isTrue();
        assertThat(response.getRecruitStartDate()).isEqualTo(LocalDate.of(2026, 6, 1));
        assertThat(response.getRecruitEndDate()).isEqualTo(LocalDate.of(2026, 6, 10));
        assertThat(response.getExpectedRunningDistanceKm()).isEqualByComparingTo("7.50");
        assertThat(response.getAdditionalQuestions()).hasSize(1);
        assertThat(response.getViewer()).isNull();
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

    private Event createEvent(String organizer) {
        return Event.builder()
                .id(1L)
                .organizer(organizer)
                .recruitStartDate(LocalDate.of(2026, 6, 1))
                .recruitEndDate(LocalDate.of(2026, 6, 10))
                .name("상계천천히달리기")
                .recruitStatus(EventRecruitStatus.RECRUIT_OPEN)
                .isPrivate(true)
                .isApprove(true)
                .type(EventType.TRAINING)
                .startTime(LocalDateTime.of(2026, 6, 20, 9, 0))
                .endTime(LocalDateTime.of(2026, 6, 20, 11, 0))
                .maxNumV(4)
                .maxNumG(4)
                .place("서울")
                .content("내용")
                .status(EventStatus.EVENT_UPCOMING)
                .eventCategory(EventCategory.GENERAL)
                .expectedRunningDistanceKm(new BigDecimal("7.50"))
                .build();
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
