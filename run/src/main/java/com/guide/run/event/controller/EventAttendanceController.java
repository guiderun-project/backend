package com.guide.run.event.controller;


import com.guide.run.event.entity.dto.response.attend.AttendanceCancelResponse;
import com.guide.run.event.entity.dto.response.attend.AttendanceUpdateResponse;
import com.guide.run.event.entity.dto.response.attend.AttendedGuideListResponse;
import com.guide.run.event.entity.dto.response.attend.EventAttendanceResponse;
import com.guide.run.event.service.EventAttendService;
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
@Tag(name = "Event Attendance", description = "이벤트 출석 체크와 신청 현황 조회 API")
@SecurityRequirement(name = "bearerAuth")
public class EventAttendanceController {
    private final EventAttendService eventAttendService;
    private final JwtProvider jwtProvider;
    @Operation(summary = "출석 현황 조회", description = "출석하기 페이지 렌더링. 그룹 구분 없이 출석 대기와 출석 완료 참가자를 반환합니다.")
    @GetMapping("/{eventId}/attendance")
    public ResponseEntity<EventAttendanceResponse> getEventAttendance(@PathVariable("eventId") Long eventId,
                                                                      HttpServletRequest request) {
        return ResponseEntity.ok().body(eventAttendService.getEventAttendance(eventId));
    }

    @Operation(summary = "출석 처리", description = "참가자를 출석 완료 상태로 설정합니다. 이미 출석 상태이면 현재 summary만 반환합니다.")
    @PostMapping("/{eventId}/attendance/{userId}")
    public ResponseEntity<AttendanceUpdateResponse> confirmAttend(@PathVariable("eventId") Long eventId,
                                                                  @PathVariable("userId") String userId,
                                                                  HttpServletRequest request) {
        return ResponseEntity.ok().body(eventAttendService.confirmAttend(eventId, userId));
    }

    @Operation(summary = "출석 취소", description = "출석 완료 참가자를 출석 대기 상태로 되돌립니다. 이미 미출석 상태이면 현재 summary만 반환합니다.")
    @DeleteMapping("/{eventId}/attendance/{userId}")
    public ResponseEntity<AttendanceCancelResponse> cancelAttend(@PathVariable("eventId") Long eventId,
                                                                 @PathVariable("userId") String userId,
                                                                 HttpServletRequest request) {
        return ResponseEntity.ok().body(eventAttendService.cancelAttend(eventId, userId));
    }

    @Operation(summary = "출석한 가이드러너 명단 조회", description = "ADMIN 전용. 해당 이벤트에서 출석 완료한 가이드러너 목록을 반환합니다.")
    @GetMapping("/{eventId}/attendance/guides")
    public ResponseEntity<AttendedGuideListResponse> getAttendedGuides(@PathVariable("eventId") Long eventId,
                                                                       HttpServletRequest request) {
        String requesterPrivateId = jwtProvider.extractUserId(request);
        return ResponseEntity.ok().body(eventAttendService.getAttendedGuides(eventId, requesterPrivateId));
    }
}
