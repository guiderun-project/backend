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

import java.util.UUID;

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
        String id = UUID.randomUUID().toString();
        Vi vi = Vi.builder()
                .runningExp(true)
                .guideName("ljg")
                .uuid(id)
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
        Assertions.assertThat(vi.getUuid()).isEqualTo(userRepository.save(vi).getUuid());
    }

    /*
    *  //테스트입니다
    @PostMapping("/api")
    public void abc(){
        Vi vi1 = Vi.builder()
                .userId("aa_1")
                .role(Role.VI)
                .build();
        Vi vi2 = Vi.builder()
                .userId("aa_2")
                .role(Role.VI)
                .build();
        Guide guide1 = Guide.builder()
                .userId("gg_1")
                .role(Role.GUIDE)
                .build();
        Guide guide2 = Guide.builder()
                .userId("gg_2")
                .role(Role.GUIDE)
                .build();
        Guide guide3 = Guide.builder()
                .userId("gg_3")
                .role(Role.GUIDE)
                .build();
        userRepository.save(vi1);
        userRepository.save(vi2);
        userRepository.save(guide1);
        userRepository.save(guide2);
        userRepository.save(guide3);

        partnerRepository.save(Partner.builder()
                .viId(vi1)
                .guideId(guide1)
                .build());
        partnerRepository.save(Partner.builder()
                .viId(vi1)
                .guideId(guide2)
                .build());
        partnerRepository.save(Partner.builder()
                .viId(vi2)
                .guideId(guide2)
                .build());
        partnerRepository.save(Partner.builder()
                .viId(vi2)
                .guideId(guide3)
                .build());
        PartnerId partnerId = new PartnerId(0L, 1L);
        Partner pa = partnerRepository.findById(partnerId).orElse(null);
        if(pa!=null){
            partnerRepository.save(Partner.builder()
                    .viId(pa.getViId())
                    .guideId(pa.getGuideId())
                    .trainingCnt(pa.getTrainingCnt()+1)
                    .contestCnt(pa.getContestCnt())
                    .build()
            );
        }

    }
    * */
}