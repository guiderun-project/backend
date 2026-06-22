package com.guide.run.event.controller;


import com.guide.run.event.entity.dto.response.get.AllEventResponse;
import com.guide.run.event.entity.dto.response.get.Count;
import com.guide.run.event.entity.dto.response.get.EventsSummaryGetResponse;
import com.guide.run.event.entity.dto.response.get.MyEventResponse;
import com.guide.run.event.entity.dto.response.get.UpcomingEventResponse;
import com.guide.run.event.entity.type.CityName;
import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.event.entity.type.EventType;
import com.guide.run.event.service.EventGetService;
import com.guide.run.global.exception.event.logic.NotValidKindException;
import com.guide.run.global.exception.event.logic.NotValidSortException;
import com.guide.run.global.exception.event.logic.NotValidTypeException;
import com.guide.run.global.exception.event.logic.NotValidYearException;
import com.guide.run.global.jwt.JwtProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.guide.run.event.entity.type.EventRecruitStatus.*;
import static com.guide.run.event.entity.type.EventType.*;

@CrossOrigin(origins = {"https://guide-run-qa.netlify.app", "https://guiderun.org",
        "https://guide-run.netlify.app","https://www.guiderun.org", "http://localhost:3000"},
        maxAge = 3600)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/event")
@Tag(name = "Event", description = "이벤트 목록, 나의 이벤트, 카운트 조회 API")
@SecurityRequirement(name = "bearerAuth")
public class EventGetController {
    private final JwtProvider jwtProvider;
    private final EventGetService eventGetService;

    @Operation(summary = "다가오는 이벤트 목록 조회", description = "메인/홈 화면에서 모집 중이거나 모집 예정인 가까운 이벤트 목록을 최대 10건 조회합니다. isApply는 로그인 사용자의 신청 여부를 나타냅니다.")
    @GetMapping("/upcoming")
    public ResponseEntity<UpcomingEventResponse> getUpcomingEvents(
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(value = "page", defaultValue = "0") int page,
            HttpServletRequest request) {
        String userId = jwtProvider.tryExtractUserId(request);
        return ResponseEntity.ok(eventGetService.getUpcomingEvents(page, userId));
    }

    @Operation(summary = "이벤트 요약 조회", description = "메인페이지 상단 요약. 비회원은 올해 전체 이벤트 수와 거리, 회원은 개인 누적 참여 수와 거리도 조회합니다.")
    @GetMapping("/summary")
    public ResponseEntity<EventsSummaryGetResponse> getEventsSummary(HttpServletRequest request) {
        String userId = jwtProvider.tryExtractUserId(request);
        return ResponseEntity.status(200).body(eventGetService.getEventsSummary(userId));
    }

    @Operation(summary = "나의 이벤트 목록 조회", description = "나의 이벤트 화면에서 예정/종료 이벤트를 연도별로 조회합니다.")
    @GetMapping("/my")
    public ResponseEntity<MyEventResponse> getMyEventList(@Parameter(description = "정렬 구분", example = "UPCOMING") @RequestParam("sort") String sort
    , @Parameter(description = "조회 연도", example = "2026") @RequestParam("year") int year, HttpServletRequest request)
    {
        if(year<0){
            throw new NotValidYearException();
        }
        String userId = jwtProvider.extractUserId(request);
        MyEventResponse myEvent = eventGetService.getMyEvent(sort,year,userId);
        return ResponseEntity.status(200).body(myEvent);
    }
    private static final int PAGE_SIZE = 10;

    @Operation(summary = "전체 이벤트 개수 조회", description = "전체 이벤트 탭에서 선택한 필터 조건에 맞는 총 이벤트 개수를 조회합니다.")
    @GetMapping("/all/count")
    public ResponseEntity<Count> getAllEventListCount(
            @Parameter(description = "탭 구분", example = "UPCOMING") @RequestParam("tab") String tab,
            @Parameter(description = "이벤트 유형 필터", example = "TOTAL") @RequestParam(value = "type", defaultValue = "TOTAL") EventType type,
            @Parameter(description = "모집 상태 필터", example = "RECRUIT_ALL") @RequestParam(value = "kind", defaultValue = "RECRUIT_ALL") EventRecruitStatus kind,
            @RequestParam(value = "cityName", required = false) CityName cityName,
            HttpServletRequest request){
        tab = normalizeTab(tab);
        if(!tab.equals("UPCOMING") && !tab.equals("END") && !tab.equals("MY")) throw new NotValidSortException();
        if(!type.equals(TRAINING) && !type.equals(COMPETITION) && !type.equals(TOTAL)) throw new NotValidTypeException();
        if(!kind.equals(RECRUIT_UPCOMING) && !kind.equals(RECRUIT_OPEN) && !kind.equals(RECRUIT_CLOSE)
                && !kind.equals(RECRUIT_END) && !kind.equals(RECRUIT_ALL)) throw new NotValidKindException();
        String userId = jwtProvider.extractUserId(request);
        return ResponseEntity.status(200).
                body(Count.builder().count(eventGetService.getAllEventListCount(tab, type, kind, userId, cityName)).build());
    }

    // 명세의 tab=PAST 를 내부 sort 체계(END)로 매핑. UPCOMING/MY 는 그대로 둔다.
    private String normalizeTab(String tab){
        return "PAST".equals(tab) ? "END" : tab;
    }

    @Operation(summary = "전체 이벤트 목록 조회", description = "전체 이벤트 탭에서 선택한 필터와 페이지네이션 조건에 맞는 이벤트 목록을 조회합니다.")
    @GetMapping("/all")
    public ResponseEntity<AllEventResponse> getAllEventList(
            @Parameter(description = "탭 구분", example = "UPCOMING") @RequestParam("tab") String tab,
            @Parameter(description = "이벤트 유형 필터", example = "TOTAL") @RequestParam(value = "type", defaultValue = "TOTAL") EventType type,
            @Parameter(description = "모집 상태 필터", example = "RECRUIT_ALL") @RequestParam(value = "kind", defaultValue = "RECRUIT_ALL") EventRecruitStatus kind,
            @RequestParam(value = "cityName", required = false) CityName cityName,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") @RequestParam(value = "page", defaultValue = "0") int page,
            HttpServletRequest request){
        tab = normalizeTab(tab);
        if(!tab.equals("UPCOMING") && !tab.equals("END") && !tab.equals("MY")) throw new NotValidSortException();
        if(!type.equals(TRAINING) && !type.equals(COMPETITION) && !type.equals(TOTAL)) throw new NotValidTypeException();
        if(!kind.equals(RECRUIT_UPCOMING) && !kind.equals(RECRUIT_OPEN) && !kind.equals(RECRUIT_CLOSE)
                && !kind.equals(RECRUIT_END) && !kind.equals(RECRUIT_ALL)) throw new NotValidKindException();
        String userId = jwtProvider.tryExtractUserId(request);
        int start = page * PAGE_SIZE;
        return ResponseEntity.status(200).
                body(eventGetService.getAllEventList(PAGE_SIZE, start, tab, type, kind, userId, cityName));
    }
}
