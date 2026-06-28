package com.guide.run.global.security.config;

import com.guide.run.global.jwt.JwtProvider;
import com.guide.run.global.service.ResponseService;
import com.guide.run.user.controller.SignupInfoController;
import com.guide.run.user.dto.response.MyPageResponse;
import com.guide.run.user.dto.response.SetAccountResponse;
import com.guide.run.user.dto.response.UpdatePersonalInfoResponse;
import com.guide.run.user.dto.response.UpdateRunningInfoResponse;
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
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
        authenticateWaitUser();
        when(jwtProvider.extractUserId(any(HttpServletRequest.class))).thenReturn(PRIVATE_ID);
        when(signupInfoService.getMyPage(PRIVATE_ID)).thenReturn(MyPageResponse.builder().build());

        mockMvc.perform(get("/api/user/mypage")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + WAIT_TOKEN))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("ROLE_WAIT 회원은 계정 아이디 중복 확인을 할 수 있다")
    void waitUserCanCheckAccountDuplicated() throws Exception {
        authenticateWaitUser();
        when(jwtProvider.extractUserId(any(HttpServletRequest.class))).thenReturn(PRIVATE_ID);
        when(userService.isAccountIdExist("runner01")).thenReturn(false);

        mockMvc.perform(post("/api/user/account/duplicated")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + WAIT_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "accountId": "runner01"
                                }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("ROLE_WAIT 회원은 아이디와 비밀번호를 최초 등록할 수 있다")
    void waitUserCanSetAccount() throws Exception {
        authenticateWaitUser();
        when(jwtProvider.extractUserId(any(HttpServletRequest.class))).thenReturn(PRIVATE_ID);
        when(userService.setAccount(any(), any(), any())).thenReturn(SetAccountResponse.builder()
                .accountId("runner01")
                .build());

        mockMvc.perform(post("/api/user/account")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + WAIT_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "accountId": "runner01",
                                  "password": "Pass1234!"
                                }
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("ROLE_WAIT 회원은 개인정보를 수정할 수 있다")
    void waitUserCanUpdatePersonalInfo() throws Exception {
        authenticateWaitUser();
        when(jwtProvider.extractUserId(any(HttpServletRequest.class))).thenReturn(PRIVATE_ID);
        when(signupInfoService.updatePersonalInfo(any(), any())).thenReturn(UpdatePersonalInfoResponse.builder().build());

        mockMvc.perform(patch("/api/user/personal")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + WAIT_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "birthDate": "1995-03-15",
                                  "phoneNumber": "01012345678",
                                  "snsId": "@runner",
                                  "id1365": "runner1365"
                                }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("ROLE_WAIT 회원은 러닝 정보를 수정할 수 있다")
    void waitUserCanUpdateRunningInfo() throws Exception {
        authenticateWaitUser();
        when(jwtProvider.extractUserId(any(HttpServletRequest.class))).thenReturn(PRIVATE_ID);
        when(signupInfoService.updateRunningInfo(any(), any())).thenReturn(UpdateRunningInfoResponse.builder().build());

        mockMvc.perform(patch("/api/user/running")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + WAIT_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "recordDegree": "A",
                                  "detailRecord": "10km 55분",
                                  "hopePrefs": "서울 한강"
                                }
                                """))
                .andExpect(status().isOk());
    }

    private void authenticateWaitUser() {
        when(jwtProvider.validateTokenExpiration(WAIT_TOKEN)).thenReturn(true);
        when(jwtProvider.getAuthentication(WAIT_TOKEN)).thenReturn(new UsernamePasswordAuthenticationToken(
                PRIVATE_ID,
                "",
                List.of(new SimpleGrantedAuthority("ROLE_WAIT"))
        ));
    }
}
