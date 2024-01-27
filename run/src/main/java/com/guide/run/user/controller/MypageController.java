package com.guide.run.user.controller;

import com.guide.run.global.jwt.JwtProvider;
import com.guide.run.user.dto.GlobalUserInfoDto;
import com.guide.run.user.service.MypageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MypageController {

    private final JwtProvider jwtProvider;
    private final MypageService mypageService;
    @GetMapping("/user/personal")
    public ResponseEntity<GlobalUserInfoDto> getGlobalUserInfo(HttpServletRequest httpServletRequest){
        String privateId = jwtProvider.extractUserId(httpServletRequest);
        GlobalUserInfoDto response = mypageService.getGlobalUserInfo(privateId);

        return ResponseEntity.ok().body(response);
    }
}
