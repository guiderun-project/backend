package com.guide.run.user.service;

import com.guide.run.event.entity.Event;
import com.guide.run.event.entity.EventForm;
import com.guide.run.event.entity.repository.EventFormRepository;
import com.guide.run.event.entity.repository.EventRepository;
import com.guide.run.event.entity.type.EventFormStatus;
import com.guide.run.event.entity.type.EventType;
import com.guide.run.user.dto.PermissionDto;
import com.guide.run.user.dto.request.UpdateRunningInfoRequest;
import com.guide.run.user.dto.response.MyPageResponse;
import com.guide.run.user.dto.response.UpdateRunningInfoResponse;
import com.guide.run.user.entity.ArchiveData;
import com.guide.run.user.entity.type.UserType;
import com.guide.run.user.entity.user.User;
import com.guide.run.user.repository.ArchiveDataRepository;
import com.guide.run.user.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SignupInfoServiceTest {

    @Mock private ArchiveDataRepository archiveDataRepository;
    @Mock private UserRepository userRepository;
    // 생성자 주입에 필요한 나머지 레포지토리는 사용하지 않으므로 mock만 주입
    @Mock private com.guide.run.user.repository.SignUpInfoRepository signUpInfoRepository;
    @Mock private EventFormRepository eventFormRepository;
    @Mock private EventRepository eventRepository;

    @InjectMocks private SignupInfoService signupInfoService;

    private static final String PRIVATE_ID = "kakao_1";

    @Test
    @DisplayName("내 약관 동의 조회 시 훈련 안전 면책 동의 상태를 함께 반환한다")
    void getMyPermissionReturnsTrainingSafety() {
        User user = User.builder().userId("u1").privateId(PRIVATE_ID).build();
        ArchiveData archiveData = ArchiveData.builder()
                .privateId(PRIVATE_ID)
                .privacy(true)
                .portraitRights(true)
                .trainingSafety(false)
                .build();
        when(userRepository.findById(PRIVATE_ID)).thenReturn(Optional.of(user));
        when(archiveDataRepository.findById(PRIVATE_ID)).thenReturn(Optional.of(archiveData));

        PermissionDto response = signupInfoService.getMyPermission(PRIVATE_ID);

        assertThat(response.isPrivacy()).isTrue();
        assertThat(response.isPortraitRights()).isTrue();
        assertThat(response.isTrainingSafety()).isFalse();
    }

    @Test
    @DisplayName("기존 회원의 훈련 안전 면책 동의는 다른 약관 값을 변경하지 않고 true로 저장한다")
    void agreeTrainingSafetyPreservesExistingPermissions() {
        ArchiveData archiveData = ArchiveData.builder()
                .privateId(PRIVATE_ID)
                .privacy(true)
                .portraitRights(false)
                .trainingSafety(false)
                .build();
        when(archiveDataRepository.findById(PRIVATE_ID)).thenReturn(Optional.of(archiveData));

        PermissionDto response = signupInfoService.agreeTrainingSafety(PRIVATE_ID);

        assertThat(response.isPrivacy()).isTrue();
        assertThat(response.isPortraitRights()).isFalse();
        assertThat(response.isTrainingSafety()).isTrue();
        assertThat(archiveData.isTrainingSafety()).isTrue();
    }

    @Test
    @DisplayName("ArchiveData가 없는 사용자도 러닝 정보 수정 시 ArchiveData를 새로 생성하여 저장한다 (500 방지)")
    void updateRunningInfoCreatesArchiveWhenMissing() {
        User user = User.builder()
                .userId("u1").privateId(PRIVATE_ID).type(UserType.GUIDE).build();
        when(userRepository.findById(PRIVATE_ID)).thenReturn(Optional.of(user));
        when(archiveDataRepository.findById(PRIVATE_ID)).thenReturn(Optional.empty());

        UpdateRunningInfoRequest request = mock(UpdateRunningInfoRequest.class);
        when(request.getRecordDegree()).thenReturn("A");
        when(request.getDetailRecord()).thenReturn("10km 50분");
        when(request.getHopePrefs()).thenReturn("주말 선호");

        UpdateRunningInfoResponse response = signupInfoService.updateRunningInfo(PRIVATE_ID, request);

        assertThat(response.getRecordDegree()).isEqualTo("A");
        assertThat(response.getDetailRecord()).isEqualTo("10km 50분");
        assertThat(response.getHopePrefs()).isEqualTo("주말 선호");
        verify(archiveDataRepository).save(any(ArchiveData.class));
    }

    @Test
    @DisplayName("ArchiveData가 있으면 기존 데이터를 갱신한다")
    void updateRunningInfoUpdatesExistingArchive() {
        User user = User.builder()
                .userId("u1").privateId(PRIVATE_ID).type(UserType.VI).build();
        ArchiveData archiveData = ArchiveData.builder().privateId(PRIVATE_ID).runningPlace("한강").build();
        when(userRepository.findById(PRIVATE_ID)).thenReturn(Optional.of(user));
        when(archiveDataRepository.findById(PRIVATE_ID)).thenReturn(Optional.of(archiveData));

        UpdateRunningInfoRequest request = mock(UpdateRunningInfoRequest.class);
        when(request.getRecordDegree()).thenReturn("B");
        when(request.getDetailRecord()).thenReturn("5km");
        when(request.getHopePrefs()).thenReturn("초보");

        UpdateRunningInfoResponse response = signupInfoService.updateRunningInfo(PRIVATE_ID, request);

        assertThat(response.getHopePrefs()).isEqualTo("초보");
        assertThat(archiveData.getRunningPlace()).isEqualTo("한강");
        verify(archiveDataRepository).save(archiveData);
    }

    @Test
    @DisplayName("마이페이지 참여 수는 사용자 카운터가 아니라 신청 완료된 이벤트 기준으로 계산한다")
    void getMyPageCountsParticipationFromAppliedEventForms() {
        User user = User.builder()
                .userId("u1")
                .privateId(PRIVATE_ID)
                .type(UserType.GUIDE)
                .trainingCnt(0)
                .competitionCnt(0)
                .build();
        when(userRepository.findById(PRIVATE_ID)).thenReturn(Optional.of(user));
        when(archiveDataRepository.findById(PRIVATE_ID)).thenReturn(Optional.empty());
        when(signUpInfoRepository.findById(PRIVATE_ID)).thenReturn(Optional.empty());
        when(eventFormRepository.findAllByPrivateId(PRIVATE_ID)).thenReturn(List.of(
                eventForm(1L, EventFormStatus.APPLIED),
                eventForm(2L, EventFormStatus.APPLIED),
                eventForm(3L, EventFormStatus.APPLIED),
                eventForm(4L, EventFormStatus.CANCELED)
        ));
        when(eventRepository.findAllById(any())).thenReturn(List.of(
                event(1L, EventType.TRAINING),
                event(2L, EventType.TRAINING),
                event(3L, EventType.COMPETITION),
                event(4L, EventType.COMPETITION)
        ));

        MyPageResponse response = signupInfoService.getMyPage(PRIVATE_ID);

        assertThat(response.getParticipation().getTrainingCount()).isEqualTo(2);
        assertThat(response.getParticipation().getCompetitionCount()).isEqualTo(1);
        assertThat(response.getParticipation().getTotalCount()).isEqualTo(3);
    }

    private EventForm eventForm(Long eventId, EventFormStatus status) {
        return EventForm.builder()
                .eventId(eventId)
                .privateId(PRIVATE_ID)
                .status(status)
                .build();
    }

    private Event event(Long eventId, EventType type) {
        return Event.builder()
                .id(eventId)
                .type(type)
                .build();
    }
}
