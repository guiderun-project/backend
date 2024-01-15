package com.guide.run.user.controller;

import com.guide.run.global.jwt.JwtProvider;
import com.guide.run.user.dto.PermissionDto;
import com.guide.run.user.dto.PersonalInfoDto;
import com.guide.run.user.service.UserInfoService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/mypage/info/personal")
    public ResponseEntity<PersonalInfoDto> getPersonalInfo(HttpServletRequest httpServletRequest){
        String userId = jwtProvider.extractUserId(httpServletRequest);
        PersonalInfoDto response = userInfoService.getPersonalInfo(userId);

        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/mypage/info/personal")
    public ResponseEntity<PersonalInfoDto> editPersonalInfo(@RequestBody PersonalInfoDto personalInfoDto, HttpServletRequest httpServletRequest){
        String userId = jwtProvider.extractUserId(httpServletRequest);
        PersonalInfoDto response = userInfoService.editPersonalInfo(userId, personalInfoDto);

        return ResponseEntity.ok().body(response);
    }

}
