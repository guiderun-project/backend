package com.guide.run.user.controller;

import com.guide.run.admin.dto.EventTypeCountDto;
import com.guide.run.event.entity.dto.response.get.Count;
import com.guide.run.global.jwt.JwtProvider;
import com.guide.run.user.dto.GlobalUserInfoDto;
import com.guide.run.user.dto.request.Add1365Dto;
import com.guide.run.user.dto.response.MyPageEventList;
import com.guide.run.user.dto.response.MyPagePartnerList;
import com.guide.run.user.dto.response.ProfileResponse;
import com.guide.run.user.service.MypageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
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
    @Operation(summary = "사용자 이벤트 이력 개수 조회", description = "프로필 모달과 이벤트 이력 화면에서 특정 사용자의 이벤트 이력 개수를 조회합니다. 실제 서버 query 이름은 `kind`입니다.")
    @GetMapping("/event-history/count/{userId}")
    public ResponseEntity<Count> getMyEventCount(@PathVariable String userId,
                                                 @Parameter(description = "모집/진행 상태 필터", example = "RECRUIT_ALL")
                                                 @RequestParam(defaultValue = "RECRUIT_ALL") String kind,
                                                 @Parameter(description = "조회 연도. 0이면 전체", example = "2026")
                                                 @RequestParam(defaultValue = "0") int year){
        Count response = Count.builder()
                .count(mypageService.getMyPageEventsCount(userId, kind, year))
                .build();
        return ResponseEntity.ok().body(response);
    }
    @Operation(summary = "사용자 이벤트 이력 목록 조회", description = "프로필 모달, 마이페이지, 이벤트 이력 화면에서 특정 사용자의 이벤트 이력을 페이지네이션으로 조회합니다.")
    @GetMapping("/event-history/{userId}")
    public ResponseEntity<MyPageEventList> getMyEventList(@PathVariable String userId,
                                                          @Parameter(description = "페이지 시작 offset", example = "0")
                                                          @RequestParam(defaultValue = "0") int start,
                                                          @Parameter(description = "조회 개수", example = "10")
                                                          @RequestParam(defaultValue = "10") int limit,
                                                          @Parameter(description = "모집/진행 상태 필터", example = "RECRUIT_ALL")
                                                          @RequestParam(defaultValue = "RECRUIT_ALL") String kind,
                                                          @Parameter(description = "조회 연도. 0이면 전체", example = "2026")
                                                          @RequestParam(defaultValue = "0") int year){
        MyPageEventList response = MyPageEventList.builder()
                .items(mypageService.getMyPageEvents(userId,start,limit, kind, year))
                .build();

        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "사용자 프로필 상세 조회", description = "프로필 모달과 관리자 사용자 상세 화면에서 공개 프로필과 통계 정보를 조회합니다.")
    @GetMapping("/profile/{userId}")
    public ResponseEntity<ProfileResponse> getUserProfile(@PathVariable String userId,
                                                          HttpServletRequest request){
        String privateId = jwtProvider.extractUserId(request);
        ProfileResponse response = mypageService.getUserProfile(userId,privateId);
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "함께 뛴 파트너 목록 조회", description = "메인 화면과 파트너 목록 화면에서 특정 사용자의 파트너 목록을 조회합니다.")
    @GetMapping("/partner-list/{userId}")
    public ResponseEntity<MyPagePartnerList> getMyPartnerList(@PathVariable String userId,
                                                              @Parameter(description = "페이지 시작 offset", example = "0")
                                                              @RequestParam(defaultValue = "0") int start,
                                                              @Parameter(description = "조회 개수", example = "10")
                                                              @RequestParam(defaultValue = "10") int limit,
                                                              @Parameter(description = "정렬 기준", example = "RECENT")
                                                              @RequestParam(defaultValue = "RECENT") String sort){
        MyPagePartnerList response = MyPagePartnerList.builder()
                .items(mypageService.getMyPagePartners(userId, start, limit, sort))
                .build();
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "함께 뛴 파트너 수 조회", description = "메인 화면과 파트너 목록 화면에서 특정 사용자의 파트너 수를 조회합니다.")
    @GetMapping("/partner-list/count/{userId}")
    public ResponseEntity<Count> getMyPartnerCount(@PathVariable String userId){
        Count response = Count.builder()
                .count(mypageService.getMyPartnersCount(userId))
                .build();

        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "이벤트 유형별 참여 수 조회", description = "프로필과 통계 영역에서 훈련/대회 참여 건수를 조회합니다.")
    @GetMapping("/event-type/count/{userId}")
    public ResponseEntity<EventTypeCountDto> getEventTypeCount(@PathVariable String userId){
        return ResponseEntity.ok(mypageService.getMyPageEventTypeCount(userId));
    }

    @Operation(summary = "1365 아이디 저장", description = "정보 수정 화면에서 1365 아이디를 저장합니다.")
    @PostMapping("/personal/1365id")
    public ResponseEntity<String> add1365(HttpServletRequest httpServletRequest, @RequestBody Add1365Dto add1365Dto){
        String privateId = jwtProvider.extractUserId(httpServletRequest);
        mypageService.add1365(add1365Dto.getId1365(), privateId);
        return ResponseEntity.ok().body("");
    }
}
