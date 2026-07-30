package com.guide.run.global.webhook.appsmith.controller;

import com.guide.run.admin.dto.response.UserApprovalResult;
import com.guide.run.admin.service.UserApprovalService;
import com.guide.run.user.entity.type.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.CrossOrigin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AppsmithUserApprovalWebhookControllerTest {

    @Mock
    private UserApprovalService userApprovalService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        AppsmithUserApprovalWebhookController controller =
                new AppsmithUserApprovalWebhookController(userApprovalService, "test-secret");
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("AppSmith origin만 CORS 허용 origin으로 설정한다")
    void allowOnlyAppsmithOrigin() {
        CrossOrigin crossOrigin = AppsmithUserApprovalWebhookController.class.getAnnotation(CrossOrigin.class);

        assertThat(crossOrigin).isNotNull();
        assertThat(crossOrigin.origins()).containsExactly("https://grp.appsmith.com");
    }

    @Test
    @DisplayName("헤더 secret이 맞으면 회원 권한과 러닝그룹 변경 요청을 처리한다")
    void approveUserWithHeaderSecret() throws Exception {
        when(userApprovalService.processUserApproval(
                "user-123",
                Role.ROLE_USER,
                "C그룹",
                "admin@example.com"
        )).thenReturn(new UserApprovalResult(
                "user-123",
                Role.ROLE_USER,
                "C그룹",
                true
        ));

        mockMvc.perform(post("/webhook/appsmith/user-approval")
                        .header("X-Appsmith-Webhook-Secret", "test-secret")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userId": "user-123",
                                  "role": "ROLE_USER",
                                  "recordDegree": "C그룹",
                                  "requestedBy": "admin@example.com"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("user-123"))
                .andExpect(jsonPath("$.role").value("ROLE_USER"))
                .andExpect(jsonPath("$.recordDegree").value("C그룹"))
                .andExpect(jsonPath("$.changed").value(true));

        verify(userApprovalService).processUserApproval(
                "user-123",
                Role.ROLE_USER,
                "C그룹",
                "admin@example.com"
        );
    }

    @Test
    @DisplayName("헤더 secret으로 ROLE_REJECT 요청을 처리한다")
    void rejectUserWithHeaderSecret() throws Exception {
        when(userApprovalService.processUserApproval(
                "user-123",
                Role.ROLE_REJECT,
                "C그룹",
                "admin@example.com"
        )).thenReturn(new UserApprovalResult(
                "user-123",
                Role.ROLE_REJECT,
                "C그룹",
                true
        ));

        mockMvc.perform(post("/webhook/appsmith/user-approval")
                        .header("X-Appsmith-Webhook-Secret", "test-secret")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userId": "user-123",
                                  "role": "거절",
                                  "runningGroup": "C그룹",
                                  "requestedBy": "admin@example.com"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("ROLE_REJECT"))
                .andExpect(jsonPath("$.recordDegree").value("C그룹"))
                .andExpect(jsonPath("$.changed").value(true));
    }

    @Test
    @DisplayName("secret이 없거나 틀리면 403을 반환한다")
    void rejectInvalidSecret() throws Exception {
        mockMvc.perform(post("/webhook/appsmith/user-approval")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userId": "user-123",
                                  "role": "ROLE_USER",
                                  "recordDegree": "C그룹"
                                }
                                """))
                .andExpect(status().isForbidden());

        verifyNoInteractions(userApprovalService);
    }

    @Test
    @DisplayName("잘못된 role이면 400을 반환한다")
    void rejectInvalidRole() throws Exception {
        mockMvc.perform(post("/webhook/appsmith/user-approval")
                        .header("X-Appsmith-Webhook-Secret", "test-secret")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userId": "user-123",
                                  "role": "HOLD",
                                  "recordDegree": "C그룹"
                                }
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userApprovalService);
    }

    @Test
    @DisplayName("러닝그룹이 없으면 400을 반환한다")
    void rejectApproveRequestWithoutRecordDegree() throws Exception {
        mockMvc.perform(post("/webhook/appsmith/user-approval")
                        .header("X-Appsmith-Webhook-Secret", "test-secret")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userId": "user-123",
                                  "role": "ROLE_USER"
                                }
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userApprovalService);
    }
}
