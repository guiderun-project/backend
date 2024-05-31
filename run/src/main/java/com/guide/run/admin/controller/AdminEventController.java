package com.guide.run.admin.controller;

import com.guide.run.admin.dto.EventHistoryCountDto;
import com.guide.run.admin.dto.EventHistoryDto;
import com.guide.run.admin.dto.EventTypeCountDto;
import com.guide.run.admin.dto.response.AllEventListResponse;
import com.guide.run.admin.dto.response.EventHistoriesResponse;
import com.guide.run.admin.service.AdminEventService;
import com.guide.run.event.entity.dto.response.EventCountResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = {"https://guide-run-qa.netlify.app", "https://guiderun.org",
        "https://guide-run.netlify.app","https://www.guiderun.org", "http://localhost:3000"},
        maxAge = 3600)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminEventController {
    private final AdminEventService adminEventService;
    @GetMapping("/{userId}/event-list")
    public ResponseEntity<EventHistoriesResponse> getUserEventHistories(@PathVariable String userId,
                                                                        @RequestParam("start") int start,
                                                                        @RequestParam("limit") int limit,
                                                                        @RequestParam("year") int year,
                                                                        @RequestParam("month") int month){
        List<EventHistoryDto> eventHistoryDto = adminEventService.getEventHistory(userId, start, limit, year, month);
        return ResponseEntity.status(200).body(EventHistoriesResponse.builder()
                .items(eventHistoryDto)
                .build());
    }
    @GetMapping("/{userId}/event-list/count")
    public ResponseEntity<EventHistoryCountDto> getUserEventHistoryCount(@PathVariable String userId,
                                                                         @RequestParam("year") int year,
                                                                         @RequestParam("month") int month){
        return ResponseEntity.status(200).
                body(EventHistoryCountDto.builder().count(adminEventService.getEventHistoryCount(userId,year,month)).build());
    }

    @GetMapping("/{userId}/event-type/count")
    public ResponseEntity<EventTypeCountDto> getUserEventHistoryCount(@PathVariable String userId){
        return ResponseEntity.status(200).
                body(adminEventService.getEventTypeCount(userId));
    }

    @GetMapping("/event-list/count")
    public ResponseEntity<EventCountResponse> getUserEventHistoryCount(){
        return ResponseEntity.status(200).
                body(EventCountResponse.builder().count(adminEventService.getAllEventCount()).build());
    }
    @GetMapping("/event-list")
    public ResponseEntity<AllEventListResponse> getAllEvent(@RequestParam("start") int start,
                                                            @RequestParam("limit") int limit){
        return ResponseEntity.status(200).body(AllEventListResponse.builder().items(adminEventService.getAllEventList(start,limit)).build());
    }
}
