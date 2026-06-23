package com.guide.run.user.service;

import com.guide.run.global.exception.user.authorize.ExistUserException;
import com.guide.run.global.exception.user.dto.BlankRequiredInfoException;
import com.guide.run.global.exception.user.dto.NotAgreeTermException;
import com.guide.run.global.jwt.JwtProvider;
import com.guide.run.user.dto.request.SignupRequest;
import com.guide.run.user.dto.response.IntegratedSignupResponse;
import com.guide.run.user.entity.ArchiveData;
import com.guide.run.user.entity.type.Role;
import com.guide.run.user.entity.type.UserType;
import com.guide.run.user.entity.user.Guide;
import com.guide.run.user.entity.user.User;
import com.guide.run.user.entity.user.Vi;
import com.guide.run.user.repository.ArchiveDataRepository;
import com.guide.run.user.repository.GuideRepository;
import com.guide.run.user.repository.ViRepository;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SignupServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private ViRepository viRepository;
    @Mock private GuideRepository guideRepository;
    @Mock private ArchiveDataRepository archiveDataRepository;
    @Mock private UserService userService;
    @Mock private JwtProvider jwtProvider;

    @InjectMocks private SignupService signupService;

    private static final String PRIVATE_ID = "kakao_1";

    private SignupRequest.Common common(boolean privacy, boolean portraitRights) {
        return new SignupRequest.Common(
                "홍길동", "1990-01-01", "010-1234-5678", "@guiderun",
                "MALE", false, false, privacy, portraitRights);
    }

    private SignupRequest viRequest(boolean privacy, boolean portraitRights) {
        SignupRequest.Vi vi = new SignupRequest.Vi(
                "A", "5km 30분", "초보 환영", true, "김가이드", "올림픽공원",
                List.of("지인 소개"), "건강을 위해");
        return new SignupRequest(UserType.VI, common(privacy, portraitRights), vi, null);
    }

    private SignupRequest guideRequest() {
        SignupRequest.Guide guide = new SignupRequest.Guide(
                "B", "10km 50분", "주말 선호", true, "박비아이", "5km 30분", "3", "6:00",
                "한강공원", List.of("SNS"), "봉사하고 싶어서");
        return new SignupRequest(UserType.GUIDE, common(true, true), null, guide);
    }

    @Test
    @DisplayName("VI 통합 회원가입 성공 시 User/Vi/ArchiveData를 저장하고 토큰과 함께 응답한다")
    void viSignupSuccess() {
        when(userRepository.findById(PRIVATE_ID)).thenReturn(Optional.empty());
        when(userService.extractNumber(anyString())).thenReturn("01012345678");
        when(userService.getUUID()).thenReturn("vi_1");
        when(jwtProvider.createAccessToken(PRIVATE_ID)).thenReturn("access");
        when(jwtProvider.createRefreshToken(PRIVATE_ID)).thenReturn("refresh");

        IntegratedSignupResponse response = signupService.signup(PRIVATE_ID, viRequest(true, true));

        assertThat(response.getUserId()).isEqualTo("vi_1");
        assertThat(response.getAccessToken()).isEqualTo("access");
        assertThat(response.getRefreshToken()).isEqualTo("refresh");
        assertThat(response.getRole()).isEqualTo(Role.ROLE_WAIT.getValue());
        assertThat(response.getDisabilityType()).isEqualTo(UserType.VI);

        verify(userRepository).save(any(User.class));
        verify(viRepository).save(any(Vi.class));
        verify(archiveDataRepository).save(any(ArchiveData.class));
        verify(guideRepository, never()).save(any());
        verify(userService).signUpATA(PRIVATE_ID);
    }

    @Test
    @DisplayName("GUIDE 통합 회원가입 성공 시 Guide 정보를 저장한다")
    void guideSignupSuccess() {
        when(userRepository.findById(PRIVATE_ID)).thenReturn(Optional.empty());
        when(userService.extractNumber(anyString())).thenReturn("01012345678");
        when(userService.getUUID()).thenReturn("guide_1");
        when(jwtProvider.createAccessToken(PRIVATE_ID)).thenReturn("access");
        when(jwtProvider.createRefreshToken(PRIVATE_ID)).thenReturn("refresh");

        IntegratedSignupResponse response = signupService.signup(PRIVATE_ID, guideRequest());

        assertThat(response.getDisabilityType()).isEqualTo(UserType.GUIDE);
        verify(guideRepository).save(any(Guide.class));
        verify(viRepository, never()).save(any());
    }

    @Test
    @DisplayName("약관에 동의하지 않으면 NotAgreeTermException(400)을 던진다")
    void notAgreeTerm() {
        assertThatThrownBy(() -> signupService.signup(PRIVATE_ID, viRequest(false, true)))
                .isInstanceOf(NotAgreeTermException.class);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("disabilityType이 VI인데 vi 정보가 없으면 BlankRequiredInfoException을 던진다")
    void missingViBlock() {
        when(userRepository.findById(PRIVATE_ID)).thenReturn(Optional.empty());
        lenient().when(userService.extractNumber(anyString())).thenReturn("01012345678");
        lenient().when(userService.getUUID()).thenReturn("vi_1");

        SignupRequest request = new SignupRequest(UserType.VI, common(true, true), null, null);

        assertThatThrownBy(() -> signupService.signup(PRIVATE_ID, request))
                .isInstanceOf(BlankRequiredInfoException.class);
    }

    @Test
    @DisplayName("이미 가입을 완료한(ROLE_NEW가 아닌) 사용자는 ExistUserException을 던진다")
    void alreadyRegistered() {
        User existing = User.builder()
                .userId("u1").privateId(PRIVATE_ID).role(Role.ROLE_USER).type(UserType.VI).build();
        when(userRepository.findById(PRIVATE_ID)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> signupService.signup(PRIVATE_ID, viRequest(true, true)))
                .isInstanceOf(ExistUserException.class);
        verify(userRepository, never()).save(any());
    }
}
