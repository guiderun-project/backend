package com.guide.run.global.security.config;

import com.guide.run.global.jwt.JwtProvider;
import com.guide.run.global.service.ResponseService;
import com.guide.run.user.controller.SignupInfoController;
import com.guide.run.user.dto.response.MyPageResponse;
import com.guide.run.user.service.SignupInfoService;
import com.guide.run.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SignupInfoController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class SecurityConfigMypageAuthTest {

    private static final String WAIT_TOKEN = "wait-token";
    private static final String PRIVATE_ID = "private-id";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtProvider jwtProvider;

    @MockBean
    private SignupInfoService signupInfoService;

    @MockBean
    private UserService userService;

    @MockBean
    private ResponseService responseService;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    @DisplayName("ROLE_WAIT 회원은 마이페이지를 조회할 수 있다")
    void waitUserCanAccessMypage() throws Exception {
        when(jwtProvider.validateTokenExpiration(WAIT_TOKEN)).thenReturn(true);
        when(jwtProvider.getAuthentication(WAIT_TOKEN)).thenReturn(new UsernamePasswordAuthenticationToken(
                PRIVATE_ID,
                "",
                List.of(new SimpleGrantedAuthority("ROLE_WAIT"))
        ));
        when(jwtProvider.extractUserId(any(HttpServletRequest.class))).thenReturn(PRIVATE_ID);
        when(signupInfoService.getMyPage(PRIVATE_ID)).thenReturn(MyPageResponse.builder().build());

        mockMvc.perform(get("/api/user/mypage")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + WAIT_TOKEN))
                .andExpect(status().isOk());
    }
}
