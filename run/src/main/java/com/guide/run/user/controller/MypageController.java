package com.guide.run.user.controller;

import com.guide.run.global.jwt.JwtProvider;
import com.guide.run.user.dto.GlobalUserInfoDto;
import com.guide.run.event.entity.type.EventType;
import com.guide.run.user.dto.response.MyActivityEventsResponse;
import com.guide.run.user.dto.response.MyActivityPartnersResponse;
import com.guide.run.user.service.MypageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Tag(name = "User Info", description = "마이페이지, 프로필, 이벤트/파트너 이력 조회 API")
@SecurityRequirement(name = "bearerAuth")
public class MypageController {

    private final JwtProvider jwtProvider;
    private final MypageService mypageService;

    @Operation(summary = "현재 로그인 사용자 정보 조회", description = "앱 초기 진입 후 사용자 스토어를 구성할 때 사용하는 기본 사용자 정보 조회 API입니다.")
    @GetMapping("/personal")
    public ResponseEntity<GlobalUserInfoDto> getGlobalUserInfo(HttpServletRequest httpServletRequest){
        String privateId = jwtProvider.extractUserId(httpServletRequest);
        GlobalUserInfoDto response = mypageService.getGlobalUserInfo(privateId);

        return ResponseEntity.ok().body(response);
    }
    @Operation(summary = "나의 활동 파트너 목록 조회", description = "나와 함께 달린 참가자 목록을 조회합니다.")
    @GetMapping("/activity/partners")
    public ResponseEntity<MyActivityPartnersResponse> getActivityPartners(
            @Parameter(description = "정렬 기준 (RECENT/OLD)", example = "RECENT")
            @RequestParam(defaultValue = "RECENT") String sort,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            HttpServletRequest request) {
        String privateId = jwtProvider.extractUserId(request);
        return ResponseEntity.ok(mypageService.getActivityPartners(privateId, sort, page));
    }

    @Operation(summary = "나의 활동 이벤트 목록 조회", description = "내가 참여했거나 주최한 이벤트 목록을 조회합니다.")
    @GetMapping("/activity/events")
    public ResponseEntity<MyActivityEventsResponse> getActivityEvents(
            @Parameter(description = "이벤트 유형 필터", example = "TOTAL")
            @RequestParam(defaultValue = "TOTAL") EventType type,
            @Parameter(description = "관계 필터 (TOTAL/PARTICIPATED/HOSTED)", example = "TOTAL")
            @RequestParam(defaultValue = "TOTAL") String relation,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            HttpServletRequest request) {
        String privateId = jwtProvider.extractUserId(request);
        return ResponseEntity.ok(mypageService.getActivityEvents(privateId, type, relation, page));
    }

}
