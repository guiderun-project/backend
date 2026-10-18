package com.guide.run.user.controller;

import com.guide.run.global.cookie.service.CookieService;
import com.guide.run.global.exception.ErrorResponseFactory;
import com.guide.run.global.jwt.JwtProvider;
import com.guide.run.global.security.config.SecurityConfig;
import com.guide.run.global.service.ResponseService;
import com.guide.run.user.dto.response.OAuthCodeResponse;
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
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SignController.class)
@Import({SecurityConfig.class, ErrorResponseFactory.class})
@ActiveProfiles("test")
class SignControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtProvider jwtProvider;

    @MockBean
    private CookieService cookieService;

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
}
