package com.guide.run.user.controller;

import com.guide.run.global.jwt.JwtProvider;
import com.guide.run.user.dto.response.ImgResponse;
import com.guide.run.user.service.ImgService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "User Info", description = "프로필 이미지 업로드/삭제 API")
@SecurityRequirement(name = "bearerAuth")
public class ImgController {
    private final JwtProvider jwtProvider;
    private final ImgService imgService;

    @Operation(summary = "프로필 이미지 업로드", description = "마이페이지에서 `files` multipart 필드로 프로필 이미지를 업로드합니다.")
    @PostMapping("/user/img")
    public ResponseEntity<ImgResponse> uploadProfile(@RequestParam MultipartFile files,
                                                     HttpServletRequest request){
        String privateId = jwtProvider.extractUserId(request);

        ImgResponse img = ImgResponse.builder()
                .img(imgService.uploadProfile(privateId, files))
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(img);
    }

    @Operation(summary = "프로필 이미지 삭제", description = "현재 로그인 사용자의 프로필 이미지를 삭제합니다.")
    @DeleteMapping("/user/img")
    public ResponseEntity<String> deleteProfile(HttpServletRequest request){
        String privateId = jwtProvider.extractUserId(request);
        imgService.deleteProfile(privateId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("");
    }
}
