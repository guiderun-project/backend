package com.guide.run.admin.controller;

import com.guide.run.admin.dto.response.partner.AdminPartnerResponse;
import com.guide.run.admin.service.AdminPartnerService;
import com.guide.run.event.entity.dto.response.get.Count;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminPartnerController {
    private final AdminPartnerService adminPartnerService;

    @GetMapping("/partner-list/{userId}")
    public ResponseEntity<List<AdminPartnerResponse>> getPartnerList(@PathVariable String userId,
                                                                     @RequestParam String kind,
                                                                     @RequestParam int start,
                                                                     @RequestParam int limit){
        return ResponseEntity.ok(adminPartnerService.getPartnerList(userId,kind, start,limit));
    }
    @GetMapping("/partner-list/count/{userId}")
    public ResponseEntity<Count> getPartnerListCount(@PathVariable String userId,
                                                     @RequestParam String kind){
        return ResponseEntity.ok(adminPartnerService.getPartnerCount(userId, kind));
    }

    /*@GetMapping("/partner-type/count/{userId}")
    public getPartnerType(@PathVariable String userId){
        //todo : 보류
    }*/
}
