package com.guide.run.event.controller;

import com.guide.run.event.entity.dto.request.EventApplyRequest;
import com.guide.run.event.entity.dto.response.form.CreatedForm;
import com.guide.run.event.entity.dto.response.form.EventApplicantFormResponse;
import com.guide.run.event.entity.dto.response.form.EventApplicantListResponse;
import com.guide.run.event.entity.dto.response.form.GetAllForms;
import com.guide.run.event.entity.dto.response.form.MyEventApplyGetResponse;
import com.guide.run.event.service.EventFormService;
import com.guide.run.global.jwt.JwtProvider;
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
@Tag(name = "Event Application", description = "이벤트 신청서 조회/생성/수정/삭제 API")
@SecurityRequirement(name = "bearerAuth")
public class EventFormController {
    private final EventFormService eventFormService;
    private final JwtProvider jwtProvider;
    @Operation(summary = "이벤트 신청서 생성", description = "이벤트 신청 화면에서 현재 로그인 사용자의 신청서를 생성합니다.")
    @PostMapping("/{eventId}/form")
    public ResponseEntity<CreatedForm> createForm(@RequestBody EventApplyRequest createForm,
                                                  @PathVariable("eventId") Long eventId,
                                                  HttpServletRequest request){
        String userId = jwtProvider.extractUserId(request);
        return ResponseEntity.ok().body(CreatedForm.builder()
                .requestId(eventFormService.createForm(createForm,eventId,userId))
                .build());
    }

    @Operation(summary = "이벤트 신청서 수정", description = "이벤트 신청 수정 화면에서 기존 신청서를 수정합니다.")
    @PatchMapping("/{eventId}/form")
    public ResponseEntity<CreatedForm> patchForm(@RequestBody EventApplyRequest createForm,
                                                  @PathVariable("eventId") Long eventId,
                                                  HttpServletRequest request){
        String userId = jwtProvider.extractUserId(request);
        return ResponseEntity.ok().body(CreatedForm.builder()
                .requestId(eventFormService.patchForm(createForm,eventId,userId))
                .build());
    }

    @Operation(summary = "내 이벤트 신청서 조회", description = "신청 수정 화면에서 현재 로그인 사용자의 신청서를 조회합니다.")
    @GetMapping("/{eventId}/form")
    public ResponseEntity<MyEventApplyGetResponse> getMyForm(@PathVariable("eventId") Long eventId,
                                                             HttpServletRequest request) {
        String privateId = jwtProvider.extractUserId(request);
        return ResponseEntity.ok(eventFormService.getMyForm(eventId, privateId));
    }

    @Operation(summary = "이벤트 신청자 명단 조회", description = "이벤트 상세 화면에서 APPLIED 상태의 신청자 명단을 그룹별로 조회합니다.")
    @GetMapping("/{eventId}/forms")
    public ResponseEntity<EventApplicantListResponse> getApplicantForms(@PathVariable("eventId") Long eventId,
                                                                        HttpServletRequest request) {
        return ResponseEntity.ok(eventFormService.getApplicantForms(eventId));
    }

    @Operation(summary = "신청자 신청서 상세 조회", description = "이벤트 주최자 또는 관리자가 특정 신청자의 신청서 상세를 조회합니다.")
    @GetMapping("/{eventId}/forms/{userId}")
    public ResponseEntity<EventApplicantFormResponse> getApplicantForm(@PathVariable("eventId") Long eventId,
                                                                       @PathVariable("userId") String userId,
                                                                       HttpServletRequest request) {
        String privateId = jwtProvider.extractUserId(request);
        return ResponseEntity.ok(eventFormService.getApplicantForm(eventId, userId, privateId));
    }

    @Operation(summary = "이벤트 전체 신청자 상세 조회", description = "이벤트 상세 화면의 신청자 패널에서 VI/Guide 신청자 목록을 함께 조회합니다.")
    @GetMapping("/{eventId}/forms/all")
    public ResponseEntity<GetAllForms> getAllForms(@PathVariable("eventId") Long eventId, HttpServletRequest request){
        String privateId = jwtProvider.extractUserId(request);
        return ResponseEntity.ok().body(eventFormService.getAllForms(eventId,privateId));
    }
    @Operation(summary = "내 이벤트 신청서 삭제", description = "이벤트 상세 또는 신청 상세 화면에서 현재 로그인 사용자의 신청서를 취소합니다.")
    @DeleteMapping("/{eventId}/form")
    public ResponseEntity<String> deleteForm(@PathVariable("eventId") Long eventId,
                                             HttpServletRequest request){
        String privateId = jwtProvider.extractUserId(request);
        eventFormService.deleteForm(eventId,privateId);
        return ResponseEntity.ok("200 ok");
    }
}
