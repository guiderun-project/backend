package com.guide.run.user.service;

import com.guide.run.global.exception.user.dto.InvalidAuthNumException;
import com.guide.run.global.exception.user.dto.SmsRateLimitExceededException;
import com.guide.run.global.jwt.JwtProvider;
import com.guide.run.global.redis.AuthNumber;
import com.guide.run.global.redis.AuthNumberRepository;
import com.guide.run.global.redis.TmpToken;
import com.guide.run.global.redis.TmpTokenRepository;
import com.guide.run.global.sms.SmsRateLimiter;
import com.guide.run.global.sms.cool.CoolSmsService;
import com.guide.run.user.dto.response.TokenResponse;
import com.guide.run.user.entity.SignUpInfo;
import com.guide.run.user.entity.type.Role;
import com.guide.run.user.entity.user.User;
import com.guide.run.user.repository.SignUpInfoRepository;
import com.guide.run.user.repository.user.UserRepository;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginInfoServiceTest {

    private static final String VERIFICATION_ID = "550e8400-e29b-41d4-a716-446655440000";
    private static final String PHONE = "01012345678";

    @Mock private UserRepository userRepository;
    @Mock private SignUpInfoRepository signUpInfoRepository;
    @Mock private AuthNumberRepository authNumberRepository;
    @Mock private TmpTokenRepository tmpTokenRepository;
    @Mock private UserService userService;
    @Mock private CoolSmsService smsService;
    @Mock private JwtProvider jwtProvider;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private SmsRateLimiter smsRateLimiter;

    private LoginInfoService loginInfoService;

    @BeforeEach
    void setUp() {
        loginInfoService = new LoginInfoService(
                userRepository, signUpInfoRepository, authNumberRepository, tmpTokenRepository,
                userService, smsService, jwtProvider, passwordEncoder, smsRateLimiter);
    }

    @Test
    @DisplayName("인증번호는 6자리 숫자다")
    void smsKeyIsSixDigits() {
        String key = LoginInfoService.createSmsKey();

        assertThat(key).hasSize(6).containsOnlyDigits();
    }

    @Test
    @DisplayName("이미 연장한 인증(canExtend=false)은 다시 연장할 수 없다")
    void cannotExtendWhenAlreadyExtended() {
        AuthNumber alreadyExtended = new AuthNumber(PHONE, "111111", "password", VERIFICATION_ID)
                .withCanExtendFalse();
        when(authNumberRepository.findByVerificationId(VERIFICATION_ID))
                .thenReturn(Optional.of(alreadyExtended));

        assertThatThrownBy(() -> loginInfoService.extendVerification(VERIFICATION_ID))
                .isInstanceOf(InvalidAuthNumException.class);
    }

    @Test
    @DisplayName("세션 코드와 일치하면 임시 토큰을 발급하고, 사용한 인증번호를 삭제한다")
    void issuesTokenAndDeletesAuthNumberWhenCodeMatches() {
        AuthNumber authNumber = new AuthNumber(PHONE, "111111", "password", VERIFICATION_ID);
        User user = User.builder().privateId("kakao_1").userId("uuid").role(Role.ROLE_USER).build();
        lenient().when(authNumberRepository.findByVerificationId(VERIFICATION_ID)).thenReturn(Optional.of(authNumber));
        lenient().when(userRepository.findUserByPhoneNumber(PHONE)).thenReturn(Optional.of(user));
        lenient().when(jwtProvider.createTmpToken(PHONE, "kakao_1", "password")).thenReturn("tmp-token");

        TokenResponse response = loginInfoService.getToken(VERIFICATION_ID, "111111");

        assertThat(response.getToken()).isEqualTo("tmp-token");
        assertThat(response.getPurpose()).isEqualTo("PASSWORD");
        verify(authNumberRepository).delete(authNumber);
    }

    @Test
    @DisplayName("다른 세션에서 유효한 코드라도 이 세션의 코드와 다르면 거부한다 (전역 조회 금지)")
    void rejectsCodeThatDoesNotMatchThisSession() {
        AuthNumber thisSession = new AuthNumber(PHONE, "111111", "password", VERIFICATION_ID);
        lenient().when(authNumberRepository.findByVerificationId(VERIFICATION_ID)).thenReturn(Optional.of(thisSession));

        // "222222"가 다른 사용자의 활성 코드일지라도, 이 verificationId 세션의 코드는 "111111"이므로 거부되어야 한다.
        assertThatThrownBy(() -> loginInfoService.getToken(VERIFICATION_ID, "222222"))
                .isInstanceOf(InvalidAuthNumException.class);
    }

    @Test
    @DisplayName("오답이 누적되면 실패 횟수를 올리고, 한도를 넘으면 정답이어도 인증번호를 무효화한다")
    void invalidatesAfterTooManyAttempts() {
        AuthNumber wrongTry = new AuthNumber(PHONE, "111111", "password", VERIFICATION_ID);
        lenient().when(authNumberRepository.findByVerificationId(VERIFICATION_ID)).thenReturn(Optional.of(wrongTry));

        // 오답 1회: 실패 횟수를 증가시켜 다시 저장한다.
        assertThatThrownBy(() -> loginInfoService.getToken(VERIFICATION_ID, "000000"))
                .isInstanceOf(InvalidAuthNumException.class);
        verify(authNumberRepository).save(any(AuthNumber.class));

        // 한도(5회)에 도달한 세션은 정답이어도 무효화(삭제)되어야 한다.
        AuthNumber exhausted = new AuthNumber(PHONE, "111111", "password", VERIFICATION_ID);
        for (int i = 0; i < 5; i++) {
            exhausted = exhausted.withIncrementedAttempt();
        }
        lenient().when(authNumberRepository.findByVerificationId(VERIFICATION_ID)).thenReturn(Optional.of(exhausted));

        AuthNumber finalExhausted = exhausted;
        assertThatThrownBy(() -> loginInfoService.getToken(VERIFICATION_ID, "111111"))
                .isInstanceOf(InvalidAuthNumException.class);
        verify(authNumberRepository).delete(finalExhausted);
    }

    @Test
    @DisplayName("비밀번호 재설정에 성공하면 사용한 임시 토큰을 삭제한다 (재사용 차단)")
    void deletesTmpTokenAfterPasswordReset() {
        TmpToken tmpToken = new TmpToken("tmp-token", "kakao_1", "password");
        SignUpInfo info = SignUpInfo.builder().privateId("kakao_1").accountId("runner01").password("old").build();
        when(tmpTokenRepository.findById("tmp-token")).thenReturn(Optional.of(tmpToken));
        when(userRepository.findUserByPrivateId("kakao_1")).thenReturn(Optional.of(User.builder().privateId("kakao_1").build()));
        when(signUpInfoRepository.findById("kakao_1")).thenReturn(Optional.of(info));

        loginInfoService.createNewPassword("tmp-token", "newPass123!");

        verify(tmpTokenRepository).delete(tmpToken);
    }

    @Test
    @DisplayName("아이디 찾기에 성공하면 사용한 임시 토큰을 삭제한다 (재사용 차단)")
    void deletesTmpTokenAfterAccountIdLookup() {
        TmpToken tmpToken = new TmpToken("tmp-token", "kakao_1", "accountId");
        User user = User.builder().privateId("kakao_1").createdAt(LocalDateTime.now()).build();
        SignUpInfo info = SignUpInfo.builder().privateId("kakao_1").accountId("runner01").build();
        when(tmpTokenRepository.findById("tmp-token")).thenReturn(Optional.of(tmpToken));
        when(userRepository.findUserByPrivateId("kakao_1")).thenReturn(Optional.of(user));
        when(signUpInfoRepository.findById("kakao_1")).thenReturn(Optional.of(info));

        loginInfoService.findAccountId("tmp-token");

        verify(tmpTokenRepository).delete(tmpToken);
    }

    @Test
    @DisplayName("발송 레이트리밋에 걸리면 인증번호를 보내지 않고 거부한다")
    void rejectsSmsSendWhenRateLimited() throws Exception {
        when(userService.extractNumber("010-1234-5678")).thenReturn(PHONE);
        when(userRepository.findUserByPhoneNumber(PHONE))
                .thenReturn(java.util.Optional.of(User.builder().privateId("p").build()));
        lenient().doThrow(new SmsRateLimitExceededException())
                .when(smsRateLimiter).requireSendAllowed(PHONE);

        assertThatThrownBy(() -> loginInfoService.getNumberForAccountId("010-1234-5678"))
                .isInstanceOf(SmsRateLimitExceededException.class);
        verify(smsService, never()).sendSMS(anyString(), anyString());
    }
}
