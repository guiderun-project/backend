package com.guide.run.user.controller;

import com.guide.run.global.jwt.JwtProvider;
import com.guide.run.user.dto.GuideRunningInfoDto;
import com.guide.run.user.dto.PermissionDto;
import com.guide.run.user.dto.PersonalInfoDto;
import com.guide.run.user.dto.ViRunningInfoDto;
import com.guide.run.user.service.SignupInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "User Info", description = "회원 기본 정보, 약관 동의, 러닝 스펙 조회/수정 API")
@SecurityRequirement(name = "bearerAuth")
public class SignupInfoController {
    private final SignupInfoService signupInfoService;
    private final JwtProvider jwtProvider;

    //약관 동의 조회
    @Operation(summary = "약관 동의 조회", description = "정보 페이지와 관리자 사용자 상세 화면에서 개인정보/초상권 동의 상태를 조회합니다.")
    @GetMapping("/user/permission/{userId}")
    public ResponseEntity<PermissionDto> getPermission(@PathVariable String userId,
                                                       HttpServletRequest httpServletRequest){
        log.info(userId);
        String privateId = jwtProvider.extractUserId(httpServletRequest);
        PermissionDto response = signupInfoService.getPermission(userId, privateId);

        return ResponseEntity.ok().body(response);
    }

    //약관 동의 수정
    @Operation(summary = "약관 동의 수정", description = "정보 수정 화면에서 개인정보/초상권 동의 여부를 저장합니다.")
    @PatchMapping("/user/permission")
    public ResponseEntity<PermissionDto> editPermission(@RequestBody PermissionDto request,
                                                        HttpServletRequest httpServletRequest){
        String privateId = jwtProvider.extractUserId(httpServletRequest);
        PermissionDto response = signupInfoService.editPermission(privateId, request);

        return ResponseEntity.ok().body(response);
    }

    //인적사항 조회
    @Operation(summary = "기본 인적사항 조회", description = "정보 페이지와 관리자 상세 패널에서 회원가입 시 입력한 기본 인적사항을 조회합니다.")
    @GetMapping("/user/personal/{userId}")
    public ResponseEntity<PersonalInfoDto> getPersonalInfo(@PathVariable String userId,
                                                           HttpServletRequest httpServletRequest){
        String privateId = jwtProvider.extractUserId(httpServletRequest);
        PersonalInfoDto response = signupInfoService.getPersonalInfo(privateId, userId);

        return ResponseEntity.ok().body(response);
    }

    //인적사항 수정
    @Operation(summary = "기본 인적사항 수정", description = "정보 수정 화면에서 이름, 연락처 공개 여부, SNS 공개 여부 등 기본 인적사항을 저장합니다.")
    @PatchMapping("/user/personal")
    public ResponseEntity<PersonalInfoDto> editPersonalInfo(@RequestBody PersonalInfoDto personalInfoDto,
                                                            HttpServletRequest httpServletRequest){
        String privateId = jwtProvider.extractUserId(httpServletRequest);
        PersonalInfoDto response = signupInfoService.editPersonalInfo(privateId, personalInfoDto);

        return ResponseEntity.ok().body(response);
    }

    //러닝 스펙 조회 vi
    @Operation(summary = "VI 러닝 스펙 조회", description = "정보 페이지와 관리자 사용자 상세 화면에서 VI 사용자의 러닝 스펙을 조회합니다.")
    @GetMapping("/user/running/vi/{userId}")
    public ResponseEntity<ViRunningInfoDto> getViRunningInfo(@PathVariable String userId,
                                                             HttpServletRequest httpServletRequest){
        String privateId = jwtProvider.extractUserId(httpServletRequest);
        ViRunningInfoDto response = signupInfoService.getViRunningInfo(userId, privateId);

        return ResponseEntity.ok().body(response);
    }
    //러닝 스펙 조회 guide
    @Operation(summary = "Guide 러닝 스펙 조회", description = "정보 페이지와 관리자 사용자 상세 화면에서 Guide 사용자의 러닝 스펙을 조회합니다.")
    @GetMapping("/user/running/guide/{userId}")
    public ResponseEntity<GuideRunningInfoDto> getGuideRunningInfo(@PathVariable String userId,
                                                                   HttpServletRequest httpServletRequest){
        String privateId = jwtProvider.extractUserId(httpServletRequest);
        GuideRunningInfoDto response = signupInfoService.getGuideRunningInfo(userId, privateId);

        return ResponseEntity.ok().body(response);
    }

    //vi 러닝스펙 수정
    @Operation(summary = "VI 러닝 스펙 수정", description = "정보 수정 화면에서 VI 사용자의 러닝 스펙을 저장합니다.")
    @PatchMapping("/user/running/vi")
    public ResponseEntity<ViRunningInfoDto> editViRunningInfo(HttpServletRequest httpServletRequest,
                                                              @RequestBody ViRunningInfoDto request){
        String privateId = jwtProvider.extractUserId(httpServletRequest);
        ViRunningInfoDto response = signupInfoService.editViRunningInfo(privateId, request);
        return ResponseEntity.ok().body(response);
    }

    //guide 러닝 스펙 수정
    @Operation(summary = "Guide 러닝 스펙 수정", description = "정보 수정 화면에서 Guide 사용자의 러닝 스펙을 저장합니다.")
    @PatchMapping("/user/running/guide")
    public ResponseEntity<GuideRunningInfoDto> editGuideRunningInfo(HttpServletRequest httpServletRequest,
                                                              @RequestBody GuideRunningInfoDto request){
        String privateId = jwtProvider.extractUserId(httpServletRequest);
        GuideRunningInfoDto response = signupInfoService.editGuideRunningInfo(privateId, request);
        return ResponseEntity.ok().body(response);
    }


}
