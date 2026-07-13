package com.guide.run.user.controller;

import com.guide.run.global.jwt.JwtProvider;
import com.guide.run.user.dto.PermissionDto;
import com.guide.run.user.dto.request.AccountIdDto;
import com.guide.run.user.dto.request.SetAccountRequest;
import com.guide.run.user.dto.request.TrainingSafetyAgreementRequest;
import com.guide.run.user.dto.request.UpdatePersonalInfoRequest;
import com.guide.run.user.dto.request.UpdateRunningInfoRequest;
import com.guide.run.user.dto.request.UserBirthDatePatchRequest;
import com.guide.run.user.dto.response.MyPageResponse;
import com.guide.run.user.dto.response.IsDuplicatedResponse;
import com.guide.run.user.dto.response.SetAccountResponse;
import com.guide.run.user.dto.response.UpdatePersonalInfoResponse;
import com.guide.run.user.dto.response.UpdateRunningInfoResponse;
import com.guide.run.user.dto.response.UserBirthDatePatchResponse;
import com.guide.run.user.service.SignupInfoService;
import com.guide.run.user.service.UserService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "User Info", description = "회원 기본 정보, 약관 동의, 러닝 정보 수정 API")
@SecurityRequirement(name = "bearerAuth")
public class SignupInfoController {
    private final SignupInfoService signupInfoService;
    private final UserService userService;
    private final JwtProvider jwtProvider;

    //아이디 중복 확인 (마이페이지)
    @Operation(summary = "아이디 중복 확인", description = "마이페이지 계정 최초 설정 화면에서 accountId 사용 가능 여부를 확인합니다.")
    @PostMapping("/user/account/duplicated")
    public ResponseEntity<IsDuplicatedResponse> checkAccountDuplicated(@RequestBody AccountIdDto request,
                                                                        HttpServletRequest httpServletRequest) {
        jwtProvider.extractUserId(httpServletRequest); // 회원 인증 확인
        IsDuplicatedResponse response = IsDuplicatedResponse.builder()
                .isUnique(!userService.isAccountIdExist(request.getAccountId()))
                .build();
        return ResponseEntity.ok(response);
    }

    //아이디/비밀번호 최초 설정
    @Operation(summary = "아이디/비밀번호 최초 설정", description = "소셜 로그인 이후 아이디/비밀번호가 없는 회원이 계정을 최초 설정합니다. 이미 설정된 경우 또는 중복 accountId는 409를 반환합니다.")
    @PostMapping("/user/account")
    public ResponseEntity<SetAccountResponse> setAccount(@RequestBody @Valid SetAccountRequest request,
                                                         HttpServletRequest httpServletRequest) {
        String privateId = jwtProvider.extractUserId(httpServletRequest);
        return ResponseEntity.status(201).body(userService.setAccount(privateId, request.getAccountId(), request.getPassword()));
    }

    //마이페이지 통합 조회
    @Operation(summary = "마이페이지 조회", description = "마이페이지 첫 화면 표시용 프로필·참여이력·개인정보·러닝정보 통합 조회 API입니다.")
    @GetMapping("/user/mypage")
    public ResponseEntity<MyPageResponse> getMyPage(HttpServletRequest httpServletRequest) {
        String privateId = jwtProvider.extractUserId(httpServletRequest);
        return ResponseEntity.ok(signupInfoService.getMyPage(privateId));
    }

    @Operation(summary = "내 약관 동의 조회", description = "로그인한 사용자의 개인정보/초상권/훈련 안전 면책 동의 상태를 조회합니다.")
    @GetMapping("/user/permission")
    public ResponseEntity<PermissionDto> getMyPermission(HttpServletRequest httpServletRequest) {
        String privateId = jwtProvider.extractUserId(httpServletRequest);
        return ResponseEntity.ok(signupInfoService.getMyPermission(privateId));
    }

    @Operation(summary = "훈련 참여 및 안전 면책 동의", description = "기존 회원이 바텀시트에서 필수 훈련 안전 면책 약관에 동의합니다.")
    @PatchMapping("/user/permission/training-safety")
    public ResponseEntity<PermissionDto> agreeTrainingSafety(
            @RequestBody @Valid TrainingSafetyAgreementRequest request,
            HttpServletRequest httpServletRequest) {
        String privateId = jwtProvider.extractUserId(httpServletRequest);
        return ResponseEntity.ok(signupInfoService.agreeTrainingSafety(privateId));
    }

    //인적사항 수정
    @Operation(summary = "내 정보 수정", description = "마이페이지 내 정보 수정 화면에서 생년월일·전화번호·SNS·1365 아이디를 수정합니다. VI 사용자가 id1365를 전송하면 400을 반환합니다.")
    @PatchMapping("/user/personal")
    public ResponseEntity<UpdatePersonalInfoResponse> editPersonalInfo(@RequestBody UpdatePersonalInfoRequest request,
                                                                       HttpServletRequest httpServletRequest) {
        String privateId = jwtProvider.extractUserId(httpServletRequest);
        return ResponseEntity.ok(signupInfoService.updatePersonalInfo(privateId, request));
    }

    //생년월일 등록
    @Operation(summary = "생년월일 등록", description = "생년월일 정보가 없는 기존 회원에게 생년월일을 등록합니다.")
    @PatchMapping("/user/personal/birth-date")
    public ResponseEntity<UserBirthDatePatchResponse> updateBirthDate(@RequestBody UserBirthDatePatchRequest request,
                                                                      HttpServletRequest httpServletRequest) {
        String privateId = jwtProvider.extractUserId(httpServletRequest);
        return ResponseEntity.ok(signupInfoService.updateBirthDate(privateId, request.getBirthDate()));
    }

    //러닝 정보 수정 (통합)
    @Operation(summary = "러닝 정보 수정", description = "마이페이지 러닝 정보 수정 화면에서 VI/Guide 공통 필드(등급·세부기록·희망지역)를 수정합니다.")
    @PatchMapping("/user/running")
    public ResponseEntity<UpdateRunningInfoResponse> updateRunningInfo(HttpServletRequest httpServletRequest,
                                                                       @RequestBody UpdateRunningInfoRequest request) {
        String privateId = jwtProvider.extractUserId(httpServletRequest);
        return ResponseEntity.ok(signupInfoService.updateRunningInfo(privateId, request));
    }


}
