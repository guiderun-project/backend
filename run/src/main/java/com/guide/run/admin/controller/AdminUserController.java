package com.guide.run.admin.controller;

import com.guide.run.admin.dto.condition.UserSortCond;
import com.guide.run.admin.dto.request.ApproveRequest;
import com.guide.run.admin.dto.response.*;
import com.guide.run.admin.dto.response.user.AdminUserList;
import com.guide.run.admin.dto.response.user.NewUserList;
import com.guide.run.admin.service.AdminUserService;

import com.guide.run.event.entity.dto.response.get.Count;
import com.guide.run.global.jwt.JwtProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@Tag(name = "Admin User", description = "관리자 회원 목록, 검색, 승인 관련 API")
@SecurityRequirement(name = "bearerAuth")
public class AdminUserController {
    private final AdminUserService adminUserService;
    private final JwtProvider jwtProvider;

    //전체 회원
    @Operation(summary = "관리자 회원 목록 조회", description = "관리자 회원 목록 화면에서 필터와 페이지네이션 조건에 맞는 회원 목록을 조회합니다.")
    @GetMapping("/user-list")
    public ResponseEntity<AdminUserList> getUserList(@RequestParam int start,
                                                     @RequestParam int limit,

                                                     @RequestParam(defaultValue = "2") int time,
                                                     @RequestParam(defaultValue = "2") int type,
                                                     @RequestParam(defaultValue = "2") int gender,
                                                     @RequestParam(defaultValue = "2") int name_team,
                                                     @RequestParam(defaultValue = "2") int approval
                                                      ){
        UserSortCond cond = UserSortCond.builder()
                .approval(approval)
                .name_team(name_team)
                .gender(gender)
                .time(time)
                .type(type)
                .build();
        AdminUserList response = AdminUserList.builder()
                        .items(adminUserService.getUserList(start, limit, cond))
                        .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "관리자 회원 목록 개수 조회", description = "관리자 회원 목록 화면의 전체 건수를 조회합니다.")
    @GetMapping("/user-list/count")
    public ResponseEntity<Count> getUserListCount(){
        Count response = adminUserService.getUserListCount();
        return ResponseEntity.ok().body(response);
    }
    @Operation(summary = "최근 가입 회원 조회", description = "관리자 메인 화면에서 최근 가입 회원 목록을 조회합니다.")
    @GetMapping("/new-user")
    public ResponseEntity<NewUserList> getNewUserList(@RequestParam int start,
                                                      @RequestParam int limit,
                                                      HttpServletRequest request){
        String privateId = jwtProvider.extractUserId(request);
        NewUserList response = NewUserList.builder()
                        .items(adminUserService.getNewUser(start,limit, privateId))
                        .build();
        return ResponseEntity.ok().body(response);
    }

    //단일 회원
    @Operation(summary = "VI 신청 상세 조회", description = "관리자 사용자 상세에서 VI 신청 상세를 조회합니다.")
    @GetMapping("/apply/vi/{userId}")
    public ResponseEntity<ViApplyResponse> getApplyVi(@PathVariable String userId){
        ViApplyResponse response = adminUserService.getApplyVi(userId);
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "Guide 신청 상세 조회", description = "관리자 사용자 상세에서 Guide 신청 상세를 조회합니다.")
    @GetMapping("/apply/guide/{userId}")
    public ResponseEntity<GuideApplyResponse> getApplyGuide(@PathVariable String userId){
        GuideApplyResponse response = adminUserService.getApplyGuide(userId);
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "회원 승인/반려 처리", description = "관리자 사용자 상세 다이얼로그에서 회원 승인 상태를 변경합니다.")
    @PostMapping("/approval-user/{userId}")
    public ResponseEntity<UserApprovalResponse> approveUser(@PathVariable String userId, @RequestBody ApproveRequest request){
        UserApprovalResponse response = adminUserService.approveUser(userId, request);
        return ResponseEntity.ok().body(response);
    }

}
