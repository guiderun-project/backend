package com.guide.run.user.controller;

import com.guide.run.global.jwt.JwtProvider;
import com.guide.run.user.dto.PermissionDto;
import com.guide.run.user.service.UserInfoService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserInfoController {
    private final UserInfoService userInfoService;
    private final JwtProvider jwtProvider;

    @GetMapping("/mypage/info/permission")
    public ResponseEntity<PermissionDto> getPermission(HttpServletRequest httpServletRequest){
        String userId = jwtProvider.extractUserId(httpServletRequest);
        PermissionDto response = userInfoService.getPermission(userId);

        return ResponseEntity.ok().body(response);
    }
}
