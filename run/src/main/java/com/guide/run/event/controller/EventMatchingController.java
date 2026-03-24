package com.guide.run.event.controller;

import com.guide.run.event.entity.dto.response.match.*;
import com.guide.run.event.service.EventMatchingService;
import com.guide.run.global.jwt.JwtProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = {"https://guide-run-qa.netlify.app", "https://guiderun.org",
        "https://guide-run.netlify.app","https://www.guiderun.org", "http://localhost:3000"},
        maxAge = 3600)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/event")
@Tag(name = "Event Matching", description = "이벤트 신청자 매칭 조회/실행 API")
@SecurityRequirement(name = "bearerAuth")
public class EventMatchingController {
    private final JwtProvider jwtProvider;
    private final EventMatchingService eventMatchingService;

    @Operation(summary = "수동 매칭 생성", description = "이벤트 상세의 매칭 패널에서 VI와 Guide를 수동으로 매칭합니다.")
    @PostMapping("{eventId}/match/{viId}/{userId}")
    public ResponseEntity<String> matchUser(@PathVariable("eventId") Long eventId,
                                            @PathVariable("viId") String viId,
                                            @PathVariable("userId") String userId,
                                            HttpServletRequest request){
        jwtProvider.extractUserId(request);
        eventMatchingService.matchUser(eventId,viId,userId);
        return ResponseEntity.ok().body("200 ok");
    }
    @Operation(summary = "매칭 취소", description = "이벤트 상세의 매칭 패널에서 특정 사용자 매칭을 해제합니다.")
    @DeleteMapping("{eventId}/match/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable("eventId") Long eventId,
                                            @PathVariable("userId") String userId,
                                            HttpServletRequest request){
        jwtProvider.extractUserId(request);
        eventMatchingService.deleteMatchUser(eventId,userId);
        return ResponseEntity.ok().body("200 ok");
    }
    @Operation(summary = "미매칭 사용자 수 조회", description = "이벤트 상세의 매칭 패널에서 미매칭 VI/Guide 수를 조회합니다.")
    @GetMapping("{eventId}/match/not/count")
    public ResponseEntity<UserTypeCount> getUserTypeCount(@PathVariable("eventId") Long eventId,
                                                          HttpServletRequest request){
        jwtProvider.extractUserId(request);
        return ResponseEntity.ok().body(eventMatchingService.getUserTypeCount(eventId));
    }
    @Operation(summary = "미매칭 사용자 목록 조회", description = "이벤트 상세의 매칭 패널에서 아직 매칭되지 않은 신청자 목록을 조회합니다.")
    @GetMapping("{eventId}/match/list")
    public ResponseEntity<NotMatchList> getNotMatchList(@PathVariable("eventId") Long eventId,
                                                         HttpServletRequest request){
        jwtProvider.extractUserId(request);
        return ResponseEntity.ok().body(NotMatchList.builder()
                .notMatch(eventMatchingService.getNotMatchList(eventId)).build());
    }
    @Operation(summary = "특정 VI의 매칭된 Guide 수 조회", description = "매칭 패널에서 특정 VI에 연결된 Guide 수를 조회합니다.")
    @GetMapping("{eventId}/match/{viId}/count")
    public ResponseEntity<MatchedGuideCount> getMatchedGuideCount(@PathVariable("eventId") Long eventId,
                                                                  @PathVariable("viId") String viId,
                                                                  HttpServletRequest request){
        jwtProvider.extractUserId(request);
        return ResponseEntity.ok().body(eventMatchingService.getMatchedGuideCount(eventId,viId));
    }
    @Operation(summary = "특정 VI의 매칭된 Guide 목록 조회", description = "매칭 패널과 매칭 박스에서 특정 VI에 연결된 Guide 목록을 조회합니다.")
    @GetMapping("{eventId}/match/{viId}/list")
    public ResponseEntity<MatchedGuideList> getMatchedGuideList(@PathVariable("eventId") Long eventId,
                                                                @PathVariable("viId") String viId,
                                                                HttpServletRequest request){
        jwtProvider.extractUserId(request);
        return ResponseEntity.ok().body(eventMatchingService.getMatchedGuideList(eventId,viId));
    }
    @Operation(summary = "매칭된 VI 수 조회", description = "이벤트 상세의 매칭 패널에서 매칭이 존재하는 VI 수를 조회합니다.")
    @GetMapping("{eventId}/match/vi/count")
    public ResponseEntity<MatchedViCount> getMatchedViCount(@PathVariable("eventId") Long eventId,
                                                               HttpServletRequest request){
        jwtProvider.extractUserId(request);
        return ResponseEntity.ok().body(eventMatchingService.getMatchedViCount(eventId));
    }
    @Operation(summary = "매칭된 VI 목록 조회", description = "이벤트 상세의 매칭 패널에서 매칭이 존재하는 VI 목록을 조회합니다.")
    @GetMapping("{eventId}/match/vi/list")
    public ResponseEntity<MatchedViList> getMatchedViList(@PathVariable("eventId") Long eventId,
                                                            HttpServletRequest request){
        jwtProvider.extractUserId(request);
        return ResponseEntity.ok().body(eventMatchingService.getMatchedViList(eventId));
    }
    @Operation(summary = "자동 매칭 실행", description = "이벤트 상세의 매칭 패널에서 현재 신청자 기준 자동 매칭을 실행합니다.")
    @PostMapping("{eventId}/match/auto")
    public ResponseEntity<String> autoMatchUsers(@PathVariable("eventId") Long eventId,
                                                 HttpServletRequest request){
        jwtProvider.extractUserId(request);
        eventMatchingService.autoMatchUsers(eventId);
        return ResponseEntity.ok().body("200 ok");
    }

}
