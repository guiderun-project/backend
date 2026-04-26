package com.guide.run.admin.service;

import com.guide.run.admin.dto.response.UserApprovalResult;
import com.guide.run.global.sms.cool.CoolSmsService;
import com.guide.run.user.entity.type.Role;
import com.guide.run.user.entity.type.UserType;
import com.guide.run.user.entity.user.User;
import com.guide.run.user.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserApprovalServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CoolSmsService coolSmsService;

    @InjectMocks
    private UserApprovalService userApprovalService;

    @Test
    @DisplayName("ROLE_WAIT 회원에게 거절이 아닌 권한을 부여하면 러닝그룹을 저장하고 승인 문자를 발송한다")
    void processUserApprovalSendsSmsWhenWaitingUserGetsApprovedRole() {
        User user = createUser(Role.ROLE_WAIT, UserType.GUIDE, "B");
        when(userRepository.findUserByUserId("user-123")).thenReturn(Optional.of(user));

        UserApprovalResult result = userApprovalService.processUserApproval(
                "user-123",
                Role.ROLE_USER,
                "C그룹",
                "admin@example.com"
        );

        assertThat(result.role()).isEqualTo(Role.ROLE_USER);
        assertThat(result.recordDegree()).isEqualTo("C그룹");
        assertThat(result.changed()).isTrue();
        assertThat(user.getRole()).isEqualTo(Role.ROLE_USER);
        assertThat(user.getRecordDegree()).isEqualTo("C그룹");
        verify(userRepository).save(user);
        verify(coolSmsService).sendToNewUser("01012345678", "홍길동", "가이드러너", "C그룹");
    }

    @Test
    @DisplayName("ROLE_WAIT 회원을 ROLE_REJECT로 변경하면 러닝그룹은 저장하고 문자는 발송하지 않는다")
    void processUserApprovalDoesNotSendSmsWhenWaitingUserGetsRejected() {
        User user = createUser(Role.ROLE_WAIT, UserType.VI, "B");
        when(userRepository.findUserByUserId("user-123")).thenReturn(Optional.of(user));

        UserApprovalResult result = userApprovalService.processUserApproval(
                "user-123",
                Role.ROLE_REJECT,
                "C그룹",
                "admin@example.com"
        );

        assertThat(result.role()).isEqualTo(Role.ROLE_REJECT);
        assertThat(result.recordDegree()).isEqualTo("C그룹");
        assertThat(result.changed()).isTrue();
        assertThat(user.getRole()).isEqualTo(Role.ROLE_REJECT);
        assertThat(user.getRecordDegree()).isEqualTo("C그룹");
        verify(userRepository).save(user);
        verifyNoInteractions(coolSmsService);
    }

    @Test
    @DisplayName("이미 같은 권한과 러닝그룹이면 중복 저장하지 않는다")
    void processUserApprovalSkipsSameValues() {
        User user = createUser(Role.ROLE_USER, UserType.GUIDE, "C그룹");
        when(userRepository.findUserByUserId("user-123")).thenReturn(Optional.of(user));

        UserApprovalResult result = userApprovalService.processUserApproval(
                "user-123",
                Role.ROLE_USER,
                "C그룹",
                "admin@example.com"
        );

        assertThat(result.changed()).isFalse();
        assertThat(result.role()).isEqualTo(Role.ROLE_USER);
        verifyNoInteractions(coolSmsService);
    }

    @Test
    @DisplayName("이미 승인된 회원의 권한이나 러닝그룹을 바꿔도 승인 문자는 발송하지 않는다")
    void processUserApprovalDoesNotSendSmsWhenPreviousRoleWasNotWait() {
        User user = createUser(Role.ROLE_USER, UserType.GUIDE, "A");
        when(userRepository.findUserByUserId("user-123")).thenReturn(Optional.of(user));

        UserApprovalResult result = userApprovalService.processUserApproval(
                "user-123",
                Role.ROLE_COACH,
                "C그룹",
                "admin@example.com"
        );

        assertThat(result.role()).isEqualTo(Role.ROLE_COACH);
        assertThat(result.recordDegree()).isEqualTo("C그룹");
        assertThat(result.changed()).isTrue();
        verify(userRepository).save(user);
        verifyNoInteractions(coolSmsService);
    }

    private User createUser(Role role, UserType type, String recordDegree) {
        return User.builder()
                .userId("user-123")
                .privateId("private-123")
                .role(role)
                .type(type)
                .name("홍길동")
                .phoneNumber("01012345678")
                .recordDegree(recordDegree)
                .build();
    }
}
