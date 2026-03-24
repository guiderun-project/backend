package com.guide.run.event.controller;

import com.guide.run.event.entity.dto.request.EventCreateRequest;
import com.guide.run.event.entity.dto.response.EventCreatedResponse;
import com.guide.run.event.entity.dto.response.EventPopUpResponse;
import com.guide.run.event.entity.dto.response.EventUpdatedResponse;
import com.guide.run.event.entity.dto.response.get.DetailEvent;
import com.guide.run.event.entity.dto.response.get.MyEventDdayResponse;
import com.guide.run.event.service.EventService;
import com.guide.run.global.jwt.JwtProvider;
import com.guide.run.global.scheduler.SchedulerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/event")
@Tag(name = "Event", description = "이벤트 생성, 수정, 삭제와 상세 조회 API")
@SecurityRequirement(name = "bearerAuth")
public class EventController {
    private final JwtProvider jwtProvider;
    private final EventService eventService;

    private final SchedulerService schedulerService;

    @Operation(summary = "이벤트 생성", description = "신규 이벤트 생성 화면에서 이벤트를 등록합니다. 생성 직후 스케줄러 등록이 함께 수행됩니다.")
    @PostMapping
    public ResponseEntity<EventCreatedResponse> eventCreate(@RequestBody EventCreateRequest request, HttpServletRequest httpServletRequest){
        String privateId = jwtProvider.extractUserId(httpServletRequest);
        EventCreatedResponse eventCreatedResponse = eventService.eventCreate(request, privateId);
        //스케줄러에 등록
        schedulerService.createSchedule(eventCreatedResponse.getEventId());
        return ResponseEntity.status(HttpStatus.CREATED).body(eventCreatedResponse);
    }
    @Operation(summary = "이벤트 수정", description = "이벤트 수정 화면에서 기존 이벤트를 수정합니다. 수정 후 스케줄러 정보도 함께 갱신됩니다.")
    @PatchMapping("/{eventId}")
    public ResponseEntity<EventUpdatedResponse> eventUpdate(@PathVariable Long eventId,@RequestBody EventCreateRequest request, HttpServletRequest httpServletRequest){
        String priavateId = jwtProvider.extractUserId(httpServletRequest);
        EventUpdatedResponse eventUpdatedResponse = eventService.eventUpdate(request, priavateId,eventId);
        //스케줄러에 등록
        schedulerService.createSchedule(eventUpdatedResponse.getEventId());
        return ResponseEntity.status(HttpStatus.OK).body(eventUpdatedResponse);
    }

    @Operation(summary = "이벤트 조기 마감", description = "이벤트 상세/수정 화면에서 모집을 조기 종료합니다.")
    @PatchMapping("/close/{eventId}")
    public ResponseEntity<String> eventClose(@PathVariable Long eventId, HttpServletRequest request){
        String privateId = jwtProvider.extractUserId(request);
        eventService.eventClose(privateId, eventId);
        schedulerService.createSchedule(eventId);
        return ResponseEntity.status(HttpStatus.OK).body("");
    }

    @Operation(summary = "이벤트 삭제", description = "관리자 이벤트 다이얼로그 또는 개설자 화면에서 이벤트를 삭제합니다.")
    @DeleteMapping("/{eventId}")
    public ResponseEntity<?> eventDelete(@PathVariable Long eventId,HttpServletRequest request){
        String userId = jwtProvider.extractUserId(request);

        eventService.eventDelete(userId,eventId);
        schedulerService.deleteSchedule(eventId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "이벤트 팝업 정보 조회", description = "이벤트 모달/팝업에서 사용하는 축약 이벤트 정보를 조회합니다.")
    @GetMapping("/pop/{eventId}")
    public ResponseEntity<EventPopUpResponse> eventPopUp(@PathVariable Long eventId, HttpServletRequest request){
        String privateId = jwtProvider.extractUserId(request);
        EventPopUpResponse response = eventService.eventPopUp(eventId, privateId);
        return ResponseEntity.ok().body(response);
    }
    @Operation(summary = "다가오는 내 이벤트 D-day 조회", description = "메인 화면 상단에서 사용할 D-day 목록을 조회합니다.")
    @GetMapping("/dday")
    public ResponseEntity<MyEventDdayResponse> getMyEventDday(HttpServletRequest request){
        String privateId = jwtProvider.extractUserId(request);
        return ResponseEntity.ok().
                body(eventService.getMyEventDday(privateId));
    }
    @Operation(summary = "이벤트 상세 조회", description = "이벤트 상세 화면과 관리자 이벤트 다이얼로그에서 사용하는 전체 이벤트 상세 정보를 조회합니다.")
    @GetMapping("/{eventId}")
    public ResponseEntity<DetailEvent> getDetailEvent(@PathVariable("eventId")Long eventId,
                                                      HttpServletRequest request){
        String privateId = jwtProvider.extractUserId(request);
        return ResponseEntity.ok().
                body(eventService.getDetailEvent(eventId,privateId));

    }
}
