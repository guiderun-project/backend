package com.guide.run.admin.controller;

import com.guide.run.admin.dto.condition.UserSortCond;
import com.guide.run.admin.dto.request.ApproveRequest;
import com.guide.run.admin.dto.response.*;
import com.guide.run.admin.dto.response.user.AdminUserList;
import com.guide.run.admin.dto.response.user.NewUserList;
import com.guide.run.admin.service.AdminUserService;

import com.guide.run.event.entity.dto.response.get.Count;
import com.guide.run.global.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminUserController {
    private final AdminUserService adminUserService;
    private final JwtProvider jwtProvider;

    //전체 회원
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

    @GetMapping("/user-list/count")
    public ResponseEntity<Count> getUserListCount(){
        Count response = adminUserService.getUserListCount();
        return ResponseEntity.ok().body(response);
    }
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
    @GetMapping("/apply/vi/{userId}")
    public ResponseEntity<ViApplyResponse> getApplyVi(@PathVariable String userId){
        ViApplyResponse response = adminUserService.getApplyVi(userId);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/apply/guide/{userId}")
    public ResponseEntity<GuideApplyResponse> getApplyGuide(@PathVariable String userId){
        GuideApplyResponse response = adminUserService.getApplyGuide(userId);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/approval-user/{userId}")
    public ResponseEntity<UserApprovalResponse> approveUser(@PathVariable String userId, @RequestBody ApproveRequest request){
        UserApprovalResponse response = adminUserService.approveUser(userId, request);
        return ResponseEntity.ok().body(response);
    }

}
