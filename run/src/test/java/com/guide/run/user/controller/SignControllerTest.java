package com.guide.run.user.controller;

import com.guide.run.global.cookie.service.CookieService;
import com.guide.run.global.exception.ErrorResponseFactory;
import com.guide.run.global.exception.auth.authorize.NotValidRefreshTokenException;
import com.guide.run.global.jwt.JwtProvider;
import com.guide.run.global.security.config.SecurityConfig;
import com.guide.run.global.service.ResponseService;
import com.guide.run.user.dto.response.OAuthCodeResponse;
import com.guide.run.user.entity.type.Role;
import com.guide.run.user.entity.type.UserType;
import com.guide.run.user.entity.user.User;
import com.guide.run.user.profile.KakaoProfile;
import com.guide.run.user.service.AppleOAuthService;
import com.guide.run.user.service.ProviderService;
import com.guide.run.user.service.SignupService;
import com.guide.run.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import jakarta.servlet.http.Cookie;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// CookieService는 실제 빈을 사용한다. 쿠키 관련 회귀는 Set-Cookie 헤더로 검증해야 의미가 있다.
@WebMvcTest(controllers = SignController.class)
@Import({SecurityConfig.class, ErrorResponseFactory.class, CookieService.class})
@ActiveProfiles("test")
class SignControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtProvider jwtProvider;

    @MockBean
    private ProviderService providerService;

    @MockBean
    private AppleOAuthService appleOAuthService;

    @MockBean
    private UserService userService;

    @MockBean
    private SignupService signupService;

    @MockBean
    private ResponseService responseService;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    @DisplayName("가입이 필요한 사용자의 카카오 로그인 응답에는 가입 전용 토큰을 담는다")
    void kakaoLoginReturnsSignupTokenForNewUser() throws Exception {
        OAuthCodeResponse kakaoToken = new OAuthCodeResponse();
        kakaoToken.setAccess_token("kakao-access-token");
        when(providerService.getAccessToken("auth-code", "kakao")).thenReturn(kakaoToken);
        when(providerService.getProfile("kakao-access-token", "kakao")).thenReturn(new KakaoProfile("kakao_1", "kakao"));
        when(userService.getUserStatus("kakao_1")).thenReturn("0");
        when(jwtProvider.createSignupToken("kakao_1")).thenReturn("signup-token");

        mockMvc.perform(post("/api/oauth/login/kakao").param("code", "auth-code"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SIGNUP_REQUIRED"))
                .andExpect(jsonPath("$.signupToken").value("signup-token"));
    }

    @Test
    @DisplayName("일반 로그인은 브라우저에 refreshToken 쿠키가 남아 있어도 새 리프레시 토큰을 발급한다")
    void generalLoginIssuesNewRefreshTokenEvenWhenStaleCookieExists() throws Exception {
        when(userService.generalLogin("runner01", "pw")).thenReturn("user_1");
        when(jwtProvider.createRefreshToken("user_1")).thenReturn("new-refresh-token");
        when(jwtProvider.createAccessToken("user_1")).thenReturn("access-token");

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accountId\":\"runner01\",\"password\":\"pw\"}")
                        .cookie(new Cookie("refreshToken", "stale-token-not-in-redis")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(header().stringValues("Set-Cookie", hasItem(containsString("refreshToken=new-refresh-token"))));
    }

    @Test
    @DisplayName("카카오 로그인은 브라우저에 refreshToken 쿠키가 남아 있어도 새 리프레시 토큰을 발급한다")
    void kakaoLoginIssuesNewRefreshTokenEvenWhenStaleCookieExists() throws Exception {
        OAuthCodeResponse kakaoToken = new OAuthCodeResponse();
        kakaoToken.setAccess_token("kakao-access-token");
        when(providerService.getAccessToken("auth-code", "kakao")).thenReturn(kakaoToken);
        when(providerService.getProfile("kakao-access-token", "kakao")).thenReturn(new KakaoProfile("kakao_1", "kakao"));
        when(userService.getUserStatus("kakao_1")).thenReturn("1");
        when(userService.findByPrivateId("kakao_1")).thenReturn(
                User.builder().privateId("kakao_1").userId("u1").role(Role.ROLE_USER).type(UserType.VI).build());
        when(jwtProvider.createRefreshToken("kakao_1")).thenReturn("new-refresh-token");
        when(jwtProvider.createAccessToken("kakao_1")).thenReturn("access-token");

        mockMvc.perform(post("/api/oauth/login/kakao").param("code", "auth-code")
                        .cookie(new Cookie("refreshToken", "stale-token-not-in-redis")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("LOGIN_SUCCESS"))
                .andExpect(header().stringValues("Set-Cookie", hasItem(containsString("refreshToken=new-refresh-token"))));
    }

    @Test
    @DisplayName("Redis에 없는 refreshToken으로 재발급을 요청하면 401과 함께 쿠키를 삭제한다")
    void reissueClearsCookieWhenRefreshTokenIsNotInRedis() throws Exception {
        when(jwtProvider.getPrivateIdForRefreshToken("stale-token-not-in-redis"))
                .thenThrow(new NotValidRefreshTokenException());

        mockMvc.perform(post("/api/oauth/login/reissue")
                        .cookie(new Cookie("refreshToken", "stale-token-not-in-redis")))
                .andExpect(status().isUnauthorized())
                .andExpect(header().stringValues("Set-Cookie", hasItem(containsString("refreshToken=;"))))
                .andExpect(header().stringValues("Set-Cookie", hasItem(containsString("Max-Age=0"))));
    }
}
