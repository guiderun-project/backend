package com.guide.run.event.service;

import com.guide.run.attendance.repository.AttendanceRepository;
import com.guide.run.event.entity.Event;
import com.guide.run.event.entity.EventForm;
import com.guide.run.event.entity.dto.request.EventApplyRequest;
import com.guide.run.event.entity.repository.EventFormRepository;
import com.guide.run.event.entity.repository.EventRepository;
import com.guide.run.event.entity.type.AdditionalQuestionType;
import com.guide.run.event.entity.type.EventCategory;
import com.guide.run.event.entity.type.EventFormStatus;
import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.event.entity.type.EventType;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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
        Event event = createEvent(EventType.COMPETITION);
        User user = createUser("user-private", UserType.VI);
        EventApplyRequest request = createApplyRequest();

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(userRepository.findUserByPrivateId("user-private")).thenReturn(Optional.of(user));
        when(eventFormRepository.findAllByEventIdAndPrivateId(1L, "user-private")).thenReturn(List.of());
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
        assertThat(savedForm.getBirthDate()).isEqualTo(LocalDate.of(1990, 1, 2));
        assertThat(savedForm.getPhoneNumber()).isEqualTo("010-1234-5678");
        assertThat(formId).isEqualTo(55L);
        verify(eventAdditionalInfoService).replaceAnswers(55L, request.getAdditionalAnswers());
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
        when(eventFormRepository.findAllByEventIdAndPrivateId(1L, "user-private")).thenReturn(List.of());

        assertThatThrownBy(() -> eventFormService.createForm(request, 1L, "user-private"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("대회 신청 정보는 필수입니다.");
        verify(eventFormRepository, never()).save(any(EventForm.class));
        verify(eventAdditionalInfoService, never()).replaceAnswers(any(), any());
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
        verify(eventAdditionalInfoService).replaceAnswers(55L, request.getAdditionalAnswers());
    }

    private Event createEvent(EventType eventType) {
        return Event.builder()
                .id(1L)
                .type(eventType)
                .recruitStatus(EventRecruitStatus.RECRUIT_OPEN)
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
