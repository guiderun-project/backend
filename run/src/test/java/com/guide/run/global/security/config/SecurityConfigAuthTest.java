package com.guide.run.global.security.config;

import com.guide.run.global.cookie.service.CookieService;
import com.guide.run.global.exception.ErrorResponseFactory;
import com.guide.run.global.jwt.JwtProvider;
import com.guide.run.global.service.ResponseService;
import com.guide.run.user.controller.SignController;
import com.guide.run.user.service.ProviderService;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SignController.class)
@Import({SecurityConfig.class, ErrorResponseFactory.class})
@ActiveProfiles("test")
class SecurityConfigAuthTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtProvider jwtProvider;

    @MockBean
    private CookieService cookieService;

    @MockBean
    private ProviderService providerService;

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
}
