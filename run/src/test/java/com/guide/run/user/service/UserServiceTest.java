package com.guide.run.user.service;

import com.guide.run.user.entity.type.Role;
import com.guide.run.user.entity.user.User;

import com.guide.run.user.repository.user.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserServiceTest {
    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    public void init() {
        userRepository.deleteAll();
    }

    @DisplayName("로그인 시 기존 회원 응답")
    @Test
    void existUserLoginResponse() {
        User user = User.builder()
                .userId(userService.getUUID())
                .userId("kakao_1")
                .role(Role.ROLE_USER)
                .build();
        userRepository.save(user);
        boolean userStatus = userService.getUserStatus("kakao_1");
        Assertions.assertThat(userStatus).isEqualTo(Role.ROLE_USER.getValue());
    }

    @DisplayName("로그인 시 가입 대기중 회원 응답")
    @Test
    void waitUserLoginResponse() {
        User user = User.builder()
                .userId(userService.getUUID())
                .userId("kakao_1")
                .role(Role.ROLE_WAIT)
                .build();
        userRepository.save(user);
        boolean userStatus = userService.getUserStatus("kakao_1");
        Assertions.assertThat(userStatus).isEqualTo(Role.ROLE_WAIT.getValue());
    }
}
