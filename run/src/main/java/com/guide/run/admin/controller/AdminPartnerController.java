package com.guide.run.admin.controller;

import com.guide.run.admin.dto.response.partner.AdminPartnerList;
import com.guide.run.admin.dto.response.partner.AdminPartnerResponse;
import com.guide.run.admin.dto.response.partner.PartnerTypeResponse;
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
    public ResponseEntity<AdminPartnerList> getPartnerList(@PathVariable String userId,
                                                              @RequestParam String kind,
                                                              @RequestParam int start,
                                                              @RequestParam int limit){
        AdminPartnerList response = AdminPartnerList.builder()
                .items(adminPartnerService.getPartnerList(userId,kind, start,limit))
                .build();

        return ResponseEntity.ok(response);
    }
    @GetMapping("/partner-list/count/{userId}")
    public ResponseEntity<Count> getPartnerListCount(@PathVariable String userId,
                                                     @RequestParam String kind){
        return ResponseEntity.ok(adminPartnerService.getPartnerCount(userId, kind));
    }

    @GetMapping("/partner-type/count/{userId}")
    public ResponseEntity<PartnerTypeResponse> getPartnerType(@PathVariable String userId){
        return ResponseEntity.ok(adminPartnerService.getPartnerType(userId));
    }
}
