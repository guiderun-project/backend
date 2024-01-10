package com.guide.run.user.controller;


import com.guide.run.user.entity.Role;
import com.guide.run.user.entity.User;
import com.guide.run.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {
    private final UserRepository userRepository;
    //USER 권한 테스트
    @GetMapping("/api/test")
    public String test(){
        return "테스트 성공";
    }
    //ADMIN 권한 테스트
    @GetMapping("/api/test2")
    public String test2(){
        return "테스트 성공";
    }
    //authentificated 테스트
    @GetMapping("/api/test3")
    public String test3(){
        return "테스트 성공";
    }
    @GetMapping("/user/create")
    public String userCreateTest(){
        userRepository.save(new User("kakao_3232984128", Role.ADMIN));
        userRepository.save(new User("bb",Role.ADMIN));
        return "생성 완료";
    }
}
