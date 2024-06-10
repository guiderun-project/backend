package com.guide.run.admin.controller;

import com.guide.run.admin.dto.EventTypeCountDto;
import com.guide.run.admin.dto.condition.EventApplyCond;
import com.guide.run.admin.dto.condition.EventSortCond;
import com.guide.run.admin.dto.request.ApprovalEvent;
import com.guide.run.admin.dto.response.event.*;
import com.guide.run.admin.service.AdminEventService;
import com.guide.run.event.entity.dto.response.get.Count;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminEventController {

    private final AdminEventService adminEventService;

    //전체 이벤트
    @GetMapping("/event-list/count")
    public ResponseEntity<Count> getAllEventCount(){
        return ResponseEntity.status(200).
                body(adminEventService.getAllEventCount());
    }
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

    @GetMapping("/current-event")
    public ResponseEntity<CurrentEventList> getCurrentEvent(@RequestParam(defaultValue = "0") int start,
                                                            @RequestParam(defaultValue = "4") int limit){
        CurrentEventList response = CurrentEventList.builder()
                .items(adminEventService.getCurrentEvents(start, limit))
                .build();
        return ResponseEntity.ok(response);
    }

    //유저 이벤트 히스토리
    @GetMapping("/event-list/{userId}")
    public ResponseEntity<AdminEventHistoryList> getUserEventHistories(@PathVariable String userId,
                                                                       @RequestParam("start") int start,
                                                                       @RequestParam("limit") int limit,
                                                                       @RequestParam(defaultValue = "all") String sort)
    {
        AdminEventHistoryList response = AdminEventHistoryList.builder()
                .items(adminEventService.getEventHistory(userId, start, limit, sort))
                .build();
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/event-list/count/{userId}")
    public ResponseEntity<Count> getUserEventHistoryCount(@PathVariable String userId,
                                                          @RequestParam(defaultValue = "all") String sort){
        Count response = adminEventService.getEventHistoryCount(userId, sort);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/event-type/count/{userId}")
    public ResponseEntity<EventTypeCountDto> getUserEventHistoryCount(@PathVariable String userId){
        return ResponseEntity.status(200).
                body(adminEventService.getEventTypeCount(userId));
    }

    //단일 이벤트
    @PostMapping("/approval-event/{eventId}")
    public ResponseEntity<String> approvalEvent(@PathVariable long eventId,
                                                @RequestBody ApprovalEvent approvalEvent){
        adminEventService.approvalEvent(eventId, approvalEvent);
        return ResponseEntity.ok().body("");
    }

    @GetMapping("event-result/{eventId}")
    public ResponseEntity<AdminEventResult> getEventResult(@PathVariable long eventId){
        return ResponseEntity.ok(adminEventService.getEventResult(eventId));
    }

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

    @GetMapping("apply-list/count/{eventId}")
    public ResponseEntity<Count> getEventApplyCount(@PathVariable long eventId){
        return ResponseEntity.ok(adminEventService.getEventApplyCount(eventId));
    }


}
