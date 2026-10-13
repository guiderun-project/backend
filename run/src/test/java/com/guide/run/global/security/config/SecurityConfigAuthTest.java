package com.guide.run.global.security.config;

import com.guide.run.global.cookie.service.CookieService;
import com.guide.run.global.exception.ErrorResponseFactory;
import com.guide.run.global.jwt.JwtProvider;
import com.guide.run.global.service.ResponseService;
import com.guide.run.user.controller.SignController;
import com.guide.run.user.service.ProviderService;
import com.guide.run.user.service.AppleOAuthService;
import com.guide.run.user.service.SignupService;
import com.guide.run.user.service.UserService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SignController.class)
@Import({SecurityConfig.class, ErrorResponseFactory.class})
@ActiveProfiles("test")
class SecurityConfigAuthTest {

    private static final String SIGNUP_TOKEN = "signup-token";

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
    @DisplayName("토큰 재발급 API는 Authorization 헤더 없이 refreshToken 쿠키로 호출할 수 있다")
    void reissueAllowsRefreshTokenCookieWithoutAuthorizationHeader() throws Exception {
        when(jwtProvider.getPrivateIdForRefreshToken("refresh-token")).thenReturn("private-id");
        when(jwtProvider.createAccessToken("private-id")).thenReturn("access-token");

        mockMvc.perform(post("/api/oauth/login/reissue")
                        .cookie(new Cookie("refreshToken", "refresh-token")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"));
    }

    @Test
    @DisplayName("가입 토큰(ROLE_NEW)으로 회원가입 API를 호출할 수 있다")
    void signupTokenCanCallSignup() throws Exception {
        authenticateSignupUser();

        mockMvc.perform(post("/api/signup")
                        .header("Authorization", "Bearer " + SIGNUP_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "disabilityType": "VI",
                                  "common": {
                                    "name": "홍길동",
                                    "phoneNumber": "01012345678",
                                    "privacy": true,
                                    "portraitRights": true,
                                    "trainingSafety": true
                                  },
                                  "vi": {}
                                }
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("가입 토큰(ROLE_NEW)으로는 회원 전용 API를 호출할 수 없다")
    void signupTokenCannotCallMemberApi() throws Exception {
        authenticateSignupUser();

        mockMvc.perform(delete("/api/withdrawal")
                        .header("Authorization", "Bearer " + SIGNUP_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    private void authenticateSignupUser() {
        when(jwtProvider.validateTokenExpiration(SIGNUP_TOKEN)).thenReturn(true);
        when(jwtProvider.getAuthentication(SIGNUP_TOKEN)).thenReturn(new UsernamePasswordAuthenticationToken(
                "kakao_1",
                "",
                List.of(new SimpleGrantedAuthority("ROLE_NEW"))
        ));
    }
}
