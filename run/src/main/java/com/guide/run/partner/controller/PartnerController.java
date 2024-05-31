package com.guide.run.partner.controller;

import com.guide.run.global.jwt.JwtProvider;
import com.guide.run.partner.service.PartnerService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PartnerController {

    private final JwtProvider jwtProvider;
    private final PartnerService partnerService;

    @PostMapping("api/user/like/{userId}")
    public ResponseEntity<String> partnerLike(@PathVariable String userId,
                                              HttpServletRequest request){
        String privateId = jwtProvider.extractUserId(request);
        partnerService.partnerLike(userId, privateId);
        return ResponseEntity.ok().body("");
    }
}
