package com.guide.run.event.controller;


import com.guide.run.event.entity.dto.response.attend.AttendCount;
import com.guide.run.event.entity.dto.response.attend.ParticipationCount;
import com.guide.run.event.entity.dto.response.attend.ParticipationInfos;
import com.guide.run.event.service.EventAttendService;
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
    @Operation(summary = "출석 체크", description = "이벤트 상세의 출석 체크 모드에서 특정 신청자를 출석 처리합니다.")
    @PostMapping("/{eventId}/attend/{userId}")
    public ResponseEntity<String> requestAttend(@PathVariable("eventId") Long eventId,
                                                @PathVariable("userId") String userId,
                                                HttpServletRequest request){
        eventAttendService.requestAttend(eventId,userId);
        return ResponseEntity.status(200).body("200 ok");
    }
    @Operation(summary = "출석 현황 개수 조회", description = "출석 체크 화면에서 출석/미출석 인원 수를 조회합니다.")
    @GetMapping("/{eventId}/attend/count")
    public ResponseEntity<AttendCount> getAttendCount(@PathVariable("eventId") Long eventId,
                                                      HttpServletRequest request){
        return ResponseEntity.ok().body(eventAttendService.getAttendCount(eventId));
    }
    @Operation(summary = "이벤트 신청 현황 개수 조회", description = "이벤트 상세의 출석/매칭 패널에서 총 신청자 수와 VI/Guide 수를 조회합니다.")
    @GetMapping("/{eventId}/forms/count")
    public ResponseEntity<ParticipationCount> getParticipationCount(@PathVariable("eventId") Long eventId,
                                                                    HttpServletRequest request){
        return ResponseEntity.ok().body(eventAttendService.getParticipationCount(eventId));
    }
    @Operation(summary = "이벤트 신청 현황 목록 조회", description = "이벤트 상세의 출석 패널에서 출석 완료/미완료 신청자 목록을 조회합니다.")
    @GetMapping("/{eventId}/forms")
    public ResponseEntity<ParticipationInfos> getParticipationInfos(@PathVariable("eventId") Long eventId,
                                                                    HttpServletRequest request){
        return ResponseEntity.ok().body(eventAttendService.getParticipationInfos(eventId));
    }
}
