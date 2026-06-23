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
    
    
    
}
