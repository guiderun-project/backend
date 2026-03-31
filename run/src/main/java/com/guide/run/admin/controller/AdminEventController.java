package com.guide.run.admin.controller;

import com.guide.run.admin.dto.EventTypeCountDto;
import com.guide.run.admin.dto.condition.EventApplyCond;
import com.guide.run.admin.dto.condition.EventSortCond;
import com.guide.run.admin.dto.request.ApprovalEvent;
import com.guide.run.admin.dto.response.Guide1365Response;
import com.guide.run.admin.dto.response.event.*;
import com.guide.run.admin.service.AdminEventService;
import com.guide.run.event.entity.dto.response.get.Count;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@Tag(name = "Admin Event", description = "관리자 이벤트 목록, 승인, 결과, 신청자 조회 API")
@SecurityRequirement(name = "bearerAuth")
public class AdminEventController {

    private final AdminEventService adminEventService;

    //전체 이벤트
    @Operation(summary = "관리자 이벤트 목록 개수 조회", description = "관리자 이벤트 목록 화면의 전체 건수를 조회합니다.")
    @GetMapping("/event-list/count")
    public ResponseEntity<Count> getAllEventCount(){
        return ResponseEntity.status(200).
                body(adminEventService.getAllEventCount());
    }
    @Operation(summary = "관리자 이벤트 목록 조회", description = "관리자 이벤트 목록 화면에서 필터와 페이지네이션 조건에 맞는 이벤트 목록을 조회합니다.")
    @GetMapping("/event-list")
    public ResponseEntity<AdminEventList> getAllEvent(@RequestParam(defaultValue = "0") int start,
                                                      @RequestParam(defaultValue = "10") int limit,
                                                      @RequestParam(defaultValue = "2") int time,
                                                      @RequestParam(defaultValue = "2") int name,
                                                      @RequestParam(defaultValue = "2") int organizer,
                                                      @RequestParam(defaultValue = "2") int approval
                                                 ){

        EventSortCond cond = EventSortCond.builder()
                .time(time)
                .approval(approval)
                .name(name)
                .organizer(organizer)
                .build();
        AdminEventList response = AdminEventList.builder()
                .items(adminEventService.getAllEventList(start, limit, cond))
                .build();
        return ResponseEntity.status(200).body(response);
    }

    @Operation(summary = "현재 진행/예정 이벤트 조회", description = "관리자 메인 화면에서 현재 노출할 이벤트 목록을 조회합니다.")
    @GetMapping("/current-event")
    public ResponseEntity<CurrentEventList> getCurrentEvent(@RequestParam(defaultValue = "0") int start,
                                                            @RequestParam(defaultValue = "4") int limit){
        CurrentEventList response = CurrentEventList.builder()
                .items(adminEventService.getCurrentEvents(start, limit))
                .build();
        return ResponseEntity.ok(response);
    }

    //유저 이벤트 히스토리
    @Operation(summary = "특정 사용자 이벤트 이력 조회", description = "관리자 사용자 상세의 이벤트 탭에서 특정 사용자의 이벤트 이력을 조회합니다.")
    @GetMapping("/event-list/{userId}")
    public ResponseEntity<AdminEventHistoryList> getUserEventHistories(@PathVariable String userId,
                                                                       @RequestParam("start") int start,
                                                                       @RequestParam("limit") int limit,
                                                                       @RequestParam(defaultValue = "all") String kind)
    {
        AdminEventHistoryList response = AdminEventHistoryList.builder()
                .items(adminEventService.getEventHistory(userId, start, limit, kind))
                .build();
        return ResponseEntity.status(200).body(response);
    }

    @Operation(summary = "특정 사용자 이벤트 이력 개수 조회", description = "관리자 사용자 상세의 이벤트 탭에서 특정 사용자의 이벤트 이력 개수를 조회합니다.")
    @GetMapping("/event-list/count/{userId}")
    public ResponseEntity<Count> getUserEventHistoryCount(@PathVariable String userId,
                                                          @RequestParam(defaultValue = "all") String kind){
        Count response = adminEventService.getEventHistoryCount(userId, kind);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "특정 사용자 이벤트 유형별 참여 수 조회", description = "관리자 사용자 상세의 이벤트 탭에서 훈련/대회 참여 건수를 조회합니다.")
    @GetMapping("/event-type/count/{userId}")
    public ResponseEntity<EventTypeCountDto> getUserEventHistoryCount(@PathVariable String userId){
        return ResponseEntity.status(200).
                body(adminEventService.getEventTypeCount(userId));
    }

    //단일 이벤트
    @Operation(summary = "이벤트 승인/반려 처리", description = "관리자 이벤트 다이얼로그에서 이벤트 승인 상태를 변경합니다.")
    @PostMapping("/approval-event/{eventId}")
    public ResponseEntity<String> approvalEvent(@PathVariable long eventId,
                                                @RequestBody ApprovalEvent approvalEvent){
        adminEventService.approvalEvent(eventId, approvalEvent);
        return ResponseEntity.ok().body("");
    }

    @Operation(summary = "이벤트 결과 요약 조회", description = "관리자 이벤트 결과 패널에서 이벤트 결과와 불참 현황을 조회합니다.")
    @GetMapping("event-result/{eventId}")
    public ResponseEntity<AdminEventResult> getEventResult(@PathVariable long eventId){
        return ResponseEntity.ok(adminEventService.getEventResult(eventId));
    }

    @Operation(summary = "이벤트 신청자 목록 조회", description = "관리자 이벤트 상세의 신청자 패널에서 필터와 페이지네이션 조건에 맞는 신청자 목록을 조회합니다.")
    @GetMapping("apply-list/{eventId}")
    public ResponseEntity<AdminEventApplyList> getEventApply(@PathVariable long eventId,
                                                             @RequestParam int start,
                                                             @RequestParam int limit,
                                                             @RequestParam(defaultValue = "2") int time,
                                                             @RequestParam(defaultValue = "2") int type_name,
                                                             @RequestParam(defaultValue = "2") int team){


        EventApplyCond cond = EventApplyCond.builder()
                .time(time)
                .type_name(type_name)
                .team(team)
                .build();

        return ResponseEntity.ok(adminEventService.getEventApply(eventId,cond, start, limit));
    }

    @Operation(summary = "이벤트 신청자 수 조회", description = "관리자 이벤트 상세의 신청자 패널에서 전체 신청자 수를 조회합니다.")
    @GetMapping("apply-list/count/{eventId}")
    public ResponseEntity<Count> getEventApplyCount(@PathVariable long eventId){
        return ResponseEntity.ok(adminEventService.getEventApplyCount(eventId));
    }


    @GetMapping("/event/{eventId}/guide/1365id")
    public ResponseEntity<List<Guide1365Response>> getGuide1365(@PathVariable long eventId){

        return ResponseEntity.ok(adminEventService.getGuide1365(eventId));
    }

}
