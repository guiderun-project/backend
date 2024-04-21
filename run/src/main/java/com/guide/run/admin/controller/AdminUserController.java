package com.guide.run.admin.controller;

import com.guide.run.admin.dto.request.ApproveRequest;
import com.guide.run.admin.dto.response.GuideApplyResponse;
import com.guide.run.admin.dto.response.UserApprovalResponse;
import com.guide.run.admin.dto.response.UserListResponse;
import com.guide.run.admin.dto.response.ViApplyResponse;
import com.guide.run.admin.service.AdminUserService;
import com.guide.run.event.entity.dto.response.search.Count;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
//@CrossOrigin(origins = {"https://guide-run-qa.netlify.app", "https://guiderun.org",
    //    "https://guide-run.netlify.app","https://www.guiderun.org", "http://localhost:3000"},
     //   maxAge = 3600)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminUserController {
    private final AdminUserService adminUserService;

    @GetMapping("/user-list")
    public ResponseEntity<UserListResponse> getUserList(@RequestParam int start,
                                                        @RequestParam int limit){
        UserListResponse response = adminUserService.getUserList(start, limit);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/user-list/count")
    public ResponseEntity<Count> getUserListCount(){
        Count response = adminUserService.getUserListCount();
        return ResponseEntity.ok().body(response);
    }

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
