package com.guide.run.user.controller;

import com.guide.run.user.dto.response.UserInfoAll.UserInfoAllResponse;
import com.guide.run.user.service.GetUserInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class UserInfoController {
    private final GetUserInfoService getUserInfoService;
    @GetMapping("api/user/info/all")
    public ResponseEntity<UserInfoAllResponse> getUserInfoAll(){
        return ResponseEntity.ok(getUserInfoService.getUserInfoAll());
    }
}
