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

    //약관 동의 조회
    @GetMapping("/user/permission/{userId}")
    public ResponseEntity<PermissionDto> getPermission(String userId){
        PermissionDto response = signupInfoService.getPermission(userId);

        return ResponseEntity.ok().body(response);
    }

    //약관 동의 수정
    @PatchMapping("/user/permission")
    public ResponseEntity<PermissionDto> editPermission(@RequestBody PermissionDto request,
                                                        HttpServletRequest httpServletRequest){
        String privateId = jwtProvider.extractUserId(httpServletRequest);
        PermissionDto response = signupInfoService.editPermission(privateId, request);

        return ResponseEntity.ok().body(response);
    }

    //인적사항 조회
    @GetMapping("/user/personal/{userId}")
    public ResponseEntity<PersonalInfoDto> getPersonalInfo(String userId){
        PersonalInfoDto response = signupInfoService.getPersonalInfo(userId);

        return ResponseEntity.ok().body(response);
    }

    //인적사항 수정
    @PatchMapping("/user/personal")
    public ResponseEntity<PersonalInfoDto> editPersonalInfo(@RequestBody PersonalInfoDto personalInfoDto,
                                                            HttpServletRequest httpServletRequest){
        String privateId = jwtProvider.extractUserId(httpServletRequest);
        PersonalInfoDto response = signupInfoService.editPersonalInfo(privateId, personalInfoDto);

        return ResponseEntity.ok().body(response);
    }

    //러닝 스펙 조회 vi
    @GetMapping("/user/running/vi/{userId}")
    public ResponseEntity<ViRunningInfoDto> getViRunningInfo(@PathVariable String userId){
        ViRunningInfoDto response = signupInfoService.getViRunningInfo(userId);

        return ResponseEntity.ok().body(response);
    }
    //러닝 스펙 조회 guide
    @GetMapping("/user/running/guide/{userId}")
    public ResponseEntity<GuideRunningInfoDto> getGuideRunningInfo(@PathVariable String userId){
        GuideRunningInfoDto response = signupInfoService.getGuideRunningInfo(userId);

        return ResponseEntity.ok().body(response);
    }

    //vi 러닝스펙 수정
    @PatchMapping("/user/running/vi")
    public ResponseEntity<ViRunningInfoDto> editViRunningInfo(HttpServletRequest httpServletRequest,
                                                              @RequestBody ViRunningInfoDto request){
        String privateId = jwtProvider.extractUserId(httpServletRequest);
        ViRunningInfoDto response = signupInfoService.editViRunningInfo(privateId, request);
        return ResponseEntity.ok().body(response);
    }

    //guide 러닝 스펙 수정
    @PatchMapping("/user/running/vi")
    public ResponseEntity<GuideRunningInfoDto> editGuideRunningInfo(HttpServletRequest httpServletRequest,
                                                              @RequestBody GuideRunningInfoDto request){
        String privateId = jwtProvider.extractUserId(httpServletRequest);
        GuideRunningInfoDto response = signupInfoService.editGuideRunningInfo(privateId, request);
        return ResponseEntity.ok().body(response);
    }


}
