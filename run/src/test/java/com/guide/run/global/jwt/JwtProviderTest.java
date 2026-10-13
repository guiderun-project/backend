package com.guide.run.global.jwt;

import com.guide.run.global.exception.user.resource.NotExistUserException;
import com.guide.run.global.redis.RefreshTokenRepository;
import com.guide.run.global.redis.TmpTokenRepository;
import com.guide.run.global.security.user.CustomUserDetails;
import com.guide.run.global.security.user.CustomUserDetailsService;
import com.guide.run.user.entity.type.Role;
import com.guide.run.user.entity.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtProviderTest {

    private static final String PRIVATE_ID = "kakao_1";

    @Mock private CustomUserDetailsService customUserDetailsService;
    @Mock private RefreshTokenRepository refreshTokenRepository;
    @Mock private TmpTokenRepository tmpTokenRepository;

    private JwtProvider jwtProvider;

    @BeforeEach
    void setUp() {
        jwtProvider = new JwtProvider(customUserDetailsService, refreshTokenRepository, tmpTokenRepository);
        ReflectionTestUtils.setField(jwtProvider, "secretKey", "test-secret-key");
        jwtProvider.init();
    }

    @Test
    @DisplayName("가입 토큰은 DB에 회원 row가 없어도 ROLE_NEW로 인증된다")
    void signupTokenAuthenticatesAsNewUserWithoutUserRow() {
        // 가입 전 사용자는 row가 없으므로 DB 조회 시 실제와 같이 예외가 발생한다.
        lenient().when(customUserDetailsService.loadUserByUsername(PRIVATE_ID))
                .thenThrow(new NotExistUserException());

        Authentication authentication = jwtProvider.getAuthentication(jwtProvider.createSignupToken(PRIVATE_ID));

        assertThat(authentication.getName()).isEqualTo(PRIVATE_ID);
        assertThat(authentication.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_NEW");
    }

    @Test
    @DisplayName("액세스 토큰은 DB에 저장된 회원 권한으로 인증된다")
    void accessTokenAuthenticatesWithStoredRole() {
        User user = User.builder()
                .privateId(PRIVATE_ID)
                .userId("user-uuid")
                .role(Role.ROLE_USER)
                .build();
        when(customUserDetailsService.loadUserByUsername(PRIVATE_ID)).thenReturn(new CustomUserDetails(user));

        Authentication authentication = jwtProvider.getAuthentication(jwtProvider.createAccessToken(PRIVATE_ID));

        assertThat(authentication.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_USER");
    }
}
