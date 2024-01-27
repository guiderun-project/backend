package com.guide.run.user.controller;

import com.guide.run.global.jwt.JwtProvider;
import com.guide.run.user.dto.GuideRunningInfoDto;
import com.guide.run.user.dto.PermissionDto;
import com.guide.run.user.dto.PersonalInfoDto;
import com.guide.run.user.dto.ViRunningInfoDto;
import com.guide.run.user.service.SignupInfoService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SignupInfoController {
    private final SignupInfoService signupInfoService;
    private final JwtProvider jwtProvider;

    @GetMapping("/user/permission/{userId}")
    public ResponseEntity<PermissionDto> getPermission(String userId,
                                                       HttpServletRequest httpServletRequest){
        String signupId = jwtProvider.extractUserId(httpServletRequest);
        PermissionDto response = signupInfoService.getPermission(userId);

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/user/personal/{userId}")
    public ResponseEntity<PersonalInfoDto> getPersonalInfo(String userId,
                                                           HttpServletRequest httpServletRequest){
        String signupId = jwtProvider.extractUserId(httpServletRequest);
        PersonalInfoDto response = signupInfoService.getPersonalInfo(userId);

        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/user/personal")
    public ResponseEntity<PersonalInfoDto> editPersonalInfo(@RequestBody PersonalInfoDto personalInfoDto,
                                                            HttpServletRequest httpServletRequest){
        String signupId = jwtProvider.extractUserId(httpServletRequest);
        PersonalInfoDto response = signupInfoService.editPersonalInfo(signupId, personalInfoDto);

        return ResponseEntity.ok().body(response);
    }

    //러닝 스펙 조회 vi
    @GetMapping("/user/running/vi/{userId}")
    public ResponseEntity<ViRunningInfoDto> getViRunningInfo(@PathVariable String userId,
                                                             HttpServletRequest httpServletRequest){
        String signupId = jwtProvider.extractUserId(httpServletRequest);
        ViRunningInfoDto response = signupInfoService.getViRunningInfo(userId, signupId);

        return ResponseEntity.ok().body(response);
    }
    //러닝 스펙 조회 guide
    @GetMapping("/user/running/guide/{userId}")
    public ResponseEntity<GuideRunningInfoDto> getGuideRunningInfo(@PathVariable String userId,
                                                                   HttpServletRequest httpServletRequest){
        String signupId = jwtProvider.extractUserId(httpServletRequest);
        GuideRunningInfoDto response = signupInfoService.getGuideRunningInfo(userId, signupId);

        return ResponseEntity.ok().body(response);
    }
}
