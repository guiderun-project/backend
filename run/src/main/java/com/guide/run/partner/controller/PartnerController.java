package com.guide.run.partner.controller;

import com.guide.run.global.jwt.JwtProvider;
import com.guide.run.partner.service.PartnerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "User Info", description = "사용자 프로필 좋아요 API")
@SecurityRequirement(name = "bearerAuth")
public class PartnerController {

    private final JwtProvider jwtProvider;
    private final PartnerService partnerService;

    @Operation(summary = "사용자 좋아요 토글", description = "프로필 모달과 프로필 페이지에서 특정 사용자의 좋아요 상태를 토글합니다.")
    @PostMapping("api/user/like/{userId}")
    public ResponseEntity<String> partnerLike(@PathVariable String userId,
                                              HttpServletRequest request){
        String privateId = jwtProvider.extractUserId(request);
        partnerService.partnerLike(userId, privateId);
        return ResponseEntity.ok().body("");
    }
}
