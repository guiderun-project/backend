package com.guide.run.event.controller;

import com.guide.run.event.entity.dto.response.search.SearchAllEventList;
import com.guide.run.event.entity.type.CityName;
import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.event.entity.type.EventType;
import com.guide.run.event.service.EventSearchService;
import com.guide.run.global.exception.event.logic.NotValidKindException;
import com.guide.run.global.exception.event.logic.NotValidSortException;
import com.guide.run.global.exception.event.logic.NotValidTypeException;
import com.guide.run.global.exception.user.resource.NotExistUserException;
import com.guide.run.global.jwt.JwtProvider;
import com.guide.run.user.repository.user.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.guide.run.event.entity.type.EventRecruitStatus.*;
import static com.guide.run.event.entity.type.EventType.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/event")
@Tag(name = "Event", description = "이벤트 검색 API")
@SecurityRequirement(name = "bearerAuth")
public class EventSearchController {
    private final JwtProvider jwtProvider;
    private final EventSearchService eventSearchService;
    private final UserRepository userRepository;

    private static final int PAGE_SIZE = 10;

    @Operation(summary = "이벤트 검색 목록 조회", description = "이벤트 검색 화면에서 키워드 및 탭 조건으로 이벤트 목록을 페이지 조회합니다. "
            + "UPCOMING/PAST 탭은 비공개 이벤트를 제외하지만, 관리자(ROLE_ADMIN)는 비공개 이벤트까지 조회합니다.")
    @GetMapping("/search")
    public SearchAllEventList searchAllEventList(
            @Parameter(description = "검색어", example = "상계천") @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @Parameter(description = "탭 구분", example = "UPCOMING") @RequestParam(value = "tab", defaultValue = "UPCOMING") String tab,
            @Parameter(description = "이벤트 유형 필터", example = "TOTAL") @RequestParam(value = "type", defaultValue = "TOTAL") EventType type,
            @Parameter(description = "모집 상태 필터", example = "RECRUIT_ALL") @RequestParam(value = "recruitStatus", required = false) EventRecruitStatus recruitStatus,
            @Parameter(description = "기존 클라이언트 호환용 모집 상태 필터", example = "RECRUIT_ALL") @RequestParam(value = "kind", required = false) EventRecruitStatus kind,
            @RequestParam(value = "cityName", required = false) CityName cityName,
            @Parameter(description = "페이지 번호 (1부터 시작)", example = "1") @RequestParam(value = "page", defaultValue = "1") int page,
            HttpServletRequest request) {
        tab = normalizeTab(tab);
        EventRecruitStatus effectiveRecruitStatus = resolveRecruitStatus(recruitStatus, kind);
        validateParams(tab, type, effectiveRecruitStatus);
        String privateId = extracted(request);
        int normalizedPage = normalizePage(page);
        int start = (normalizedPage - 1) * PAGE_SIZE;
        return eventSearchService.getSearchAllEvents(start, PAGE_SIZE, normalizedPage, keyword, tab, type, effectiveRecruitStatus, privateId, cityName);
    }

    // 명세의 tab=PAST 를 내부 sort 체계(END)로 매핑. UPCOMING/MY 는 그대로 둔다.
    private String normalizeTab(String tab) {
        return "PAST".equals(tab) ? "END" : tab;
    }

    private void validateParams(String tab, EventType type, EventRecruitStatus kind) {
        if (!tab.equals("UPCOMING") && !tab.equals("END") && !tab.equals("MY")) throw new NotValidSortException();
        if (!type.equals(TRAINING) && !type.equals(COMPETITION) && !type.equals(TOTAL)) throw new NotValidTypeException();
        if (!kind.equals(RECRUIT_UPCOMING) && !kind.equals(RECRUIT_OPEN) && !kind.equals(RECRUIT_CLOSE)
                && !kind.equals(RECRUIT_END) && !kind.equals(RECRUIT_ALL)) throw new NotValidKindException();
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

    private int normalizePage(int page) {
        return Math.max(page, 1);
    }

    // 비회원도 검색 가능. 토큰이 있으면 사용자 존재를 검증하고, 없으면 null(비회원)로 처리한다.
    private String extracted(HttpServletRequest request) {
        String privateId = jwtProvider.tryExtractUserId(request);
        if (privateId != null) {
            userRepository.findUserByPrivateId(privateId)
                    .orElseThrow(() -> new NotExistUserException());
        }
        return privateId;
    }
}
