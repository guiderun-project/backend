package com.guide.run.user.service;

import com.guide.run.user.dto.request.UpdateRunningInfoRequest;
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
    @Mock private com.guide.run.user.repository.ViRepository viRepository;
    @Mock private com.guide.run.user.repository.GuideRepository guideRepository;
    @Mock private com.guide.run.user.repository.SignUpInfoRepository signUpInfoRepository;

    @InjectMocks private SignupInfoService signupInfoService;

    private static final String PRIVATE_ID = "kakao_1";

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
}
