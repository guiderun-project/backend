package com.guide.run.admin.controller;

import com.guide.run.admin.dto.response.partner.AdminPartnerList;
import com.guide.run.admin.dto.response.partner.PartnerTypeResponse;
import com.guide.run.admin.service.AdminPartnerService;
import com.guide.run.event.entity.dto.response.get.Count;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@Tag(name = "Admin Partner", description = "관리자 사용자 상세의 파트너 이력/통계 API")
@SecurityRequirement(name = "bearerAuth")
public class AdminPartnerController {
    private final AdminPartnerService adminPartnerService;

    @Operation(summary = "사용자 파트너 이력 조회", description = "관리자 사용자 상세의 파트너 탭에서 이벤트 종류별 파트너 목록을 조회합니다.")
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
    @Operation(summary = "사용자 파트너 이력 개수 조회", description = "관리자 사용자 상세의 파트너 탭에서 이벤트 종류별 파트너 수를 조회합니다.")
    @GetMapping("/partner-list/count/{userId}")
    public ResponseEntity<Count> getPartnerListCount(@PathVariable String userId,
                                                     @RequestParam String kind){
        return ResponseEntity.ok(adminPartnerService.getPartnerCount(userId, kind));
    }

    @Operation(summary = "사용자 파트너 유형별 통계 조회", description = "관리자 사용자 상세의 파트너 탭에서 훈련/대회 기준 파트너 통계를 조회합니다.")
    @GetMapping("/partner-type/count/{userId}")
    public ResponseEntity<PartnerTypeResponse> getPartnerType(@PathVariable String userId){
        return ResponseEntity.ok(adminPartnerService.getPartnerType(userId));
    }
}
