package com.guide.run.user.controller;

import com.guide.run.user.dto.response.UserInfoAll.UserInfoAllResponse;
import com.guide.run.user.service.GetUserInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Tag(name = "User Info", description = "사용자 전체 정보 조회 API")
@SecurityRequirement(name = "bearerAuth")
public class UserInfoController {
    private final GetUserInfoService getUserInfoService;
    @Operation(summary = "전체 사용자 정보 조회", description = "Guide/VI 전체 정보를 한 번에 조회하는 내부성 성격의 API입니다.")
    @GetMapping("/api/user/info/all")
    public ResponseEntity<UserInfoAllResponse> getUserInfoAll(){
        return ResponseEntity.ok(getUserInfoService.getUserInfoAll());
    }
}
