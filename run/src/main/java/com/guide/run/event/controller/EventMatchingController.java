package com.guide.run.event.controller;

import com.guide.run.event.entity.dto.request.match.MatchingCreateRequest;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/event")
@Tag(name = "Event Matching", description = "이벤트 신청자 매칭 조회/실행 API")
@SecurityRequirement(name = "bearerAuth")
public class EventMatchingController {
    private final JwtProvider jwtProvider;
    private final EventMatchingService eventMatchingService;

    @Operation(summary = "매칭 생성 (VI 1명 + Guide 여러 명)", description = "매칭하기 페이지에서 VI 1명과 Guide 여러 명을 한 번의 요청으로 매칭합니다.")
    @PostMapping("{eventId}/matching")
    public ResponseEntity<MatchingCreateResponse> createMatching(@PathVariable("eventId") Long eventId,
                                                                 @RequestBody MatchingCreateRequest request,
                                                                 HttpServletRequest httpRequest) {
        jwtProvider.extractUserId(httpRequest);
        return ResponseEntity.ok().body(eventMatchingService.createMatching(eventId, request));
    }

    @Operation(summary = "VI 매칭 전체 취소", description = "매칭하기 페이지에서 특정 VI에 연결된 모든 Guide 매칭을 한 번에 취소합니다.")
    @DeleteMapping("{eventId}/matching/{viId}")
    public ResponseEntity<MatchingCancelResponse> cancelMatching(@PathVariable("eventId") Long eventId,
                                                                 @PathVariable("viId") String viId,
                                                                 HttpServletRequest request) {
        jwtProvider.extractUserId(request);
        return ResponseEntity.ok().body(eventMatchingService.cancelMatching(eventId, viId));
    }

    @Operation(summary = "이벤트 매칭 현황 조회", description = "내 파트너와 신청 그룹별 매칭 현황(매칭된 VI+Guide, 미매칭 Guide)을 반환합니다.")
    @GetMapping("{eventId}/matching/status")
    public ResponseEntity<EventMatchingStatusResponse> getMatchingStatus(@PathVariable("eventId") Long eventId,
                                                                         HttpServletRequest request) {
        String privateId = jwtProvider.extractUserId(request);
        return ResponseEntity.ok().body(eventMatchingService.getMatchingStatus(eventId, privateId));
    }

    @Operation(summary = "매칭 대기 참가자 조회", description = "매칭하기 페이지의 매칭 대기 탭. 아직 매칭되지 않은 참가자를 RunningGroup별로 반환합니다.")
    @GetMapping("{eventId}/matching/waiting")
    public ResponseEntity<MatchingWaitingResponse> getMatchingWaiting(@PathVariable("eventId") Long eventId,
                                                                       HttpServletRequest request) {
        jwtProvider.extractUserId(request);
        return ResponseEntity.ok().body(eventMatchingService.getMatchingWaiting(eventId));
    }

    @Operation(summary = "매칭 완료 참가자 조회", description = "매칭하기 페이지의 매칭 완료 탭. VI와 매칭된 Guide 배열을 RunningGroup별로 반환합니다.")
    @GetMapping("{eventId}/matching/completed")
    public ResponseEntity<MatchingCompletedResponse> getMatchingCompleted(@PathVariable("eventId") Long eventId,
                                                                          HttpServletRequest request) {
        jwtProvider.extractUserId(request);
        return ResponseEntity.ok().body(eventMatchingService.getMatchingCompleted(eventId));
    }

}
