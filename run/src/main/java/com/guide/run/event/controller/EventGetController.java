package com.guide.run.event.controller;


import com.guide.run.event.entity.dto.response.get.AllEventResponse;
import com.guide.run.event.entity.dto.response.get.EventsSummaryGetResponse;
import com.guide.run.event.entity.dto.response.get.UpcomingEventResponse;
import com.guide.run.event.entity.type.CityName;
import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.event.entity.type.EventType;
import com.guide.run.event.service.EventGetService;
import com.guide.run.global.exception.event.logic.NotValidKindException;
import com.guide.run.global.exception.event.logic.NotValidSortException;
import com.guide.run.global.exception.event.logic.NotValidTypeException;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/event")
@Tag(name = "Event", description = "이벤트 목록 및 요약 조회 API")
@SecurityRequirement(name = "bearerAuth")
public class EventGetController {
    private final JwtProvider jwtProvider;
    private final EventGetService eventGetService;

    @Operation(summary = "다가오는 이벤트 목록 조회", description = "메인/홈 화면에서 사용자 유형에 맞는 가까운 이벤트 목록을 조회합니다.")
    @GetMapping("/upcoming")
    public ResponseEntity<UpcomingEventResponse> getUpcomingEvents(
            @Parameter(description = "기존 클라이언트 호환용 파라미터. 응답 계산에는 사용하지 않습니다.", example = "0")
            @RequestParam(value = "page", required = false) Integer page,
            HttpServletRequest request) {
        String userId = jwtProvider.tryExtractUserId(request);
        return ResponseEntity.ok(eventGetService.getUpcomingEvents(userId));
    }

    @Operation(summary = "이벤트 요약 조회", description = "메인페이지 상단 요약. 비회원은 올해 전체 이벤트 수와 거리, 회원은 개인 누적 참여 수와 거리도 조회합니다.")
    @GetMapping("/summary")
    public ResponseEntity<EventsSummaryGetResponse> getEventsSummary(HttpServletRequest request) {
        String userId = jwtProvider.tryExtractUserId(request);
        return ResponseEntity.status(200).body(eventGetService.getEventsSummary(userId));
    }

    private static final int PAGE_SIZE = 10;

    // 명세의 tab=PAST 를 내부 sort 체계(END)로 매핑. UPCOMING/MY 는 그대로 둔다.
    private String normalizeTab(String tab){
        return "PAST".equals(tab) ? "END" : tab;
    }

    private EventRecruitStatus resolveRecruitStatus(EventRecruitStatus recruitStatus, EventRecruitStatus kind) {
        if (recruitStatus != null) {
            return recruitStatus;
        }
        if (kind != null) {
            return kind;
        }
        return RECRUIT_ALL;
    }

    private void validateRecruitStatus(EventRecruitStatus recruitStatus) {
        if(!recruitStatus.equals(RECRUIT_UPCOMING) && !recruitStatus.equals(RECRUIT_OPEN) && !recruitStatus.equals(RECRUIT_CLOSE)
                && !recruitStatus.equals(RECRUIT_END) && !recruitStatus.equals(RECRUIT_ALL)) throw new NotValidKindException();
    }

    private int normalizePage(int page) {
        return Math.max(page, 1);
    }

    @Operation(summary = "전체 이벤트 목록 조회", description = "전체 이벤트 탭에서 선택한 필터와 페이지네이션 조건에 맞는 이벤트 목록을 조회합니다. "
            + "UPCOMING/PAST 탭은 비공개 이벤트를 제외하지만, 관리자(ROLE_ADMIN)는 비공개 이벤트까지 조회합니다.")
    @GetMapping("/all")
    public ResponseEntity<AllEventResponse> getAllEventList(
            @Parameter(description = "탭 구분", example = "UPCOMING") @RequestParam("tab") String tab,
            @Parameter(description = "이벤트 유형 필터", example = "TOTAL") @RequestParam(value = "type", defaultValue = "TOTAL") EventType type,
            @Parameter(description = "모집 상태 필터", example = "RECRUIT_ALL") @RequestParam(value = "recruitStatus", required = false) EventRecruitStatus recruitStatus,
            @Parameter(description = "기존 클라이언트 호환용 모집 상태 필터", example = "RECRUIT_ALL") @RequestParam(value = "kind", required = false) EventRecruitStatus kind,
            @RequestParam(value = "cityName", required = false) CityName cityName,
            @Parameter(description = "페이지 번호 (1부터 시작)", example = "1") @RequestParam(value = "page", defaultValue = "1") int page,
            HttpServletRequest request){
        tab = normalizeTab(tab);
        EventRecruitStatus effectiveRecruitStatus = resolveRecruitStatus(recruitStatus, kind);
        if(!tab.equals("UPCOMING") && !tab.equals("END") && !tab.equals("MY")) throw new NotValidSortException();
        if(!type.equals(TRAINING) && !type.equals(COMPETITION) && !type.equals(TOTAL)) throw new NotValidTypeException();
        validateRecruitStatus(effectiveRecruitStatus);
        String userId = jwtProvider.tryExtractUserId(request);
        int normalizedPage = normalizePage(page);
        int start = (normalizedPage - 1) * PAGE_SIZE;
        return ResponseEntity.status(200).
                body(eventGetService.getAllEventList(PAGE_SIZE, start, normalizedPage, tab, type, effectiveRecruitStatus, userId, cityName));
    }
}
