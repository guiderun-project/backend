package com.guide.run.user.service;

import com.guide.run.user.entity.type.Role;
import com.guide.run.user.entity.User;
import com.guide.run.user.entity.type.UserStatus;

import com.guide.run.user.entity.Vi;
import com.guide.run.user.repository.UserRepository;
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
    public void init(){
        userRepository.deleteAll();
    }

    @DisplayName("로그인 시 기존 회원 응답")
    @Test
    void existUserLoginResponse(){
        User user = User.builder()
                .userId("kakao_1")
                .role(Role.VI)
                .build();
        userRepository.save(user);
        String userStatus = userService.getUserStatus("kakao_1");
        Assertions.assertThat(userStatus).isEqualTo(UserStatus.EXIST.getValue());
    }

    @DisplayName("로그인 시 가입 대기중 회원 응답")
    @Test
    void waitUserLoginResponse(){
        User user = User.builder()
                .userId("kakao_1")
                .role(Role.VWAIT)
                .build();
        userRepository.save(user);
        String userStatus = userService.getUserStatus("kakao_1");
        Assertions.assertThat(userStatus).isEqualTo(UserStatus.WAIT.getValue());
    }

    @DisplayName("로그인 시 신규 회원 응답")
    @Test
    void newUserLoginResponse(){
        String userStatus = userService.getUserStatus("kakao_1");
        Assertions.assertThat(userStatus).isEqualTo(UserStatus.NEW.getValue());
    }

    @DisplayName("vi 회원가입")
    @Test
    void viSignup(){
        Vi vi = Vi.builder()
                .runningExp(true)
                .guideName("ljg")
                .userId("kakao_1")
                .name("lj")
                .gender("male")
                .phoneNumber("010-9999-xxxx")
                .age(13)
                .detailRecord("45:23")
                .recordDegree("A")
                .role(Role.VWAIT)
                .snsId("XXXXX12345")
                .build();
        Assertions.assertThat(vi).isEqualTo(userRepository.save(vi));
    }
}