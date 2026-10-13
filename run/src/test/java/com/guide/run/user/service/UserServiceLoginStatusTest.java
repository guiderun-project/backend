package com.guide.run.user.service;

import com.guide.run.user.entity.type.Role;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceLoginStatusTest {

    @Mock private UserRepository userRepository;

    @InjectMocks private UserService userService;

    @Test
    @DisplayName("처음 로그인한 사용자는 가입이 필요하다고 응답하고 회원 row를 만들지 않는다")
    void newUserLoginDoesNotCreateUserRow() {
        when(userRepository.findById("kakao_1")).thenReturn(Optional.empty());

        String status = userService.getUserStatus("kakao_1");

        assertThat(status).isEqualTo("0");
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("전화번호까지 등록된 기존 회원은 로그인 성공으로 응답한다")
    void registeredUserLoginSucceeds() {
        User user = User.builder()
                .privateId("kakao_1")
                .userId("user-uuid")
                .phoneNumber("01012345678")
                .role(Role.ROLE_USER)
                .build();
        when(userRepository.findById("kakao_1")).thenReturn(Optional.of(user));

        assertThat(userService.getUserStatus("kakao_1")).isEqualTo("1");
    }
}
