package com.guide.run.admin.controller;

import com.guide.run.admin.dto.condition.UserSortCond;
import com.guide.run.admin.dto.request.ApproveRequest;
import com.guide.run.admin.dto.response.*;
import com.guide.run.admin.dto.response.user.NewUserResponse;
import com.guide.run.admin.dto.response.user.UserItem;
import com.guide.run.admin.service.AdminUserService;

import com.guide.run.event.entity.dto.response.get.Count;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminUserController {
    private final AdminUserService adminUserService;

    //전체 회원
    @GetMapping("/user-list")
    public ResponseEntity<List<UserItem>> getUserList(@RequestParam int start,
                                                      @RequestParam int limit,

                                                      @RequestParam(defaultValue = "false") boolean time,
                                                      @RequestParam(defaultValue = "false") boolean type,
                                                      @RequestParam(defaultValue = "false") boolean gender,
                                                      @RequestParam(defaultValue = "false") boolean name_team,
                                                      @RequestParam(defaultValue = "false") boolean approval
                                                      ){
        UserSortCond cond = UserSortCond.builder()
                .approval(approval)
                .name_team(name_team)
                .gender(gender)
                .time(time)
                .type(type)
                .build();
        List<UserItem> response = adminUserService.getUserList(start, limit, cond);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/user-list/count")
    public ResponseEntity<Count> getUserListCount(){
        Count response = adminUserService.getUserListCount();
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("/new-user")
    public ResponseEntity<List<NewUserResponse>> getNewUserList(@RequestParam int start, @RequestParam int limit){
        List<NewUserResponse> response = adminUserService.getNewUser(start,limit);
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
