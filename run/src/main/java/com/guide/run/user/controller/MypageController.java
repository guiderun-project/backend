package com.guide.run.user.controller;

import com.guide.run.event.entity.dto.response.search.Count;
import com.guide.run.event.entity.dto.response.search.MyEventResponse;
import com.guide.run.event.entity.dto.response.search.MyPageEventResponse;
import com.guide.run.global.jwt.JwtProvider;
import com.guide.run.user.dto.GlobalUserInfoDto;
import com.guide.run.user.dto.response.ProfileResponse;
import com.guide.run.user.service.MypageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@CrossOrigin(origins = {"https://guide-run-qa.netlify.app", "https://guiderun.org",
        "https://guide-run.netlify.app","https://www.guiderun.org", "http://localhost:3000"})
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class MypageController {

    private final JwtProvider jwtProvider;
    private final MypageService mypageService;
    @GetMapping("/personal")
    public ResponseEntity<GlobalUserInfoDto> getGlobalUserInfo(HttpServletRequest httpServletRequest){
        String privateId = jwtProvider.extractUserId(httpServletRequest);
        GlobalUserInfoDto response = mypageService.getGlobalUserInfo(privateId);

        return ResponseEntity.ok().body(response);
    }
    @GetMapping("/event-history/count/{userId}")
    public ResponseEntity<Count> getMyEventCount(@PathVariable String userId){
        Count response = Count.builder()
                .count(mypageService.getMyPageEventsCount(userId))
                .build();
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("/event-history/{userId}")
    public ResponseEntity<MyPageEventResponse> getMyEventList(@PathVariable String userId,
                                                              @RequestParam(defaultValue = "0") int start,
                                                              @RequestParam(defaultValue = "10") int limit){
        MyPageEventResponse response = mypageService.getMyPageEvents(userId,start,limit);

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<ProfileResponse> getUserProfile(@PathVariable String userId,
                                                          HttpServletRequest request){
        String privateId = jwtProvider.extractUserId(request);
        ProfileResponse response = mypageService.getUserProfile(userId,privateId);
        return ResponseEntity.ok().body(response);
    }
}
