package com.guide.run.event.controller;


import com.guide.run.event.entity.dto.response.attend.AttendCount;
import com.guide.run.event.entity.dto.response.attend.ParticipationCount;
import com.guide.run.event.entity.dto.response.attend.ParticipationInfos;
import com.guide.run.event.service.EventAttendService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/event")
public class EventAttendanceController {
    private final EventAttendService eventAttendService;
    @PostMapping("/{eventId}/attend/{userId}")
    public ResponseEntity<String> requestAttend(@PathVariable("eventId") Long eventId,
                                                @PathVariable("userId") String userId,
                                                HttpServletRequest request){
        eventAttendService.requestAttend(eventId,userId);
        return ResponseEntity.status(200).body("200 ok");
    }
    @GetMapping("/{eventId}/attend/count")
    public ResponseEntity<AttendCount> getAttendCount(@PathVariable("eventId") Long eventId,
                                                      HttpServletRequest request){
        return ResponseEntity.ok().body(eventAttendService.getAttendCount(eventId));
    }
    @GetMapping("/{eventId}/forms/count")
    public ResponseEntity<ParticipationCount> getParticipationCount(@PathVariable("eventId") Long eventId,
                                                                    HttpServletRequest request){
        return ResponseEntity.ok().body(eventAttendService.getParticipationCount(eventId));
    }
    @GetMapping("/{eventId}/forms")
    public ResponseEntity<ParticipationInfos> getParticipationInfos(@PathVariable("eventId") Long eventId,
                                                                    HttpServletRequest request){
        return ResponseEntity.ok().body(eventAttendService.getParticipationInfos(eventId));
    }
}
