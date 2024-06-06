package com.guide.run.user.controller;

import com.guide.run.global.jwt.JwtProvider;
import com.guide.run.user.service.ImgService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ImgController {
    private final JwtProvider jwtProvider;
    private final ImgService imgService;

    @PostMapping("/user/img")
    public ResponseEntity<String> uploadProfile(@RequestParam("image") MultipartFile file,
                                                HttpServletRequest request){
        String privateId = jwtProvider.extractUserId(request);

        String img = imgService.uploadProfile(privateId, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(img);
    }

    @DeleteMapping("/user/img")
    public ResponseEntity<String> deleteProfile(HttpServletRequest request){
        String privateId = jwtProvider.extractUserId(request);
        imgService.deleteProfile(privateId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("");
    }
}
