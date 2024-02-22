package com.guide.run.ap;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;
    @GetMapping("/{userId}/event-list")
    public ResponseEntity<EventHistories> getUserEventHistories(@PathVariable String userId,
                                                            @RequestParam("start") int start,
                                                            @RequestParam("limit") int limit,
                                                            @RequestParam("year") int year,
                                                            @RequestParam("month") int month){
        List<EventHistory> eventHistory = adminService.getEventHistory(userId, start, limit, year, month);
        return ResponseEntity.status(200).body(EventHistories.builder()
                .items(eventHistory)
                .build());
    }
    @GetMapping("/{userId}/event-type/count")
    public ResponseEntity<EventHistoryCount> getUserEventHistoryCount(@PathVariable String userId,
                                                            @RequestParam("year") int year,
                                                            @RequestParam("month") int month){
        return ResponseEntity.status(200).
                body(EventHistoryCount.builder().count(adminService.getEventHistoryCount(userId,year,month)).build());
    }


}
