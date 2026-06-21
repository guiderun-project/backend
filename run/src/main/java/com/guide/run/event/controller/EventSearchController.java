package com.guide.run.event.controller;

import com.guide.run.event.entity.dto.response.search.SearchAllEventsCount;
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

@CrossOrigin(origins = {"https://guide-run-qa.netlify.app", "https://guiderun.org",
        "https://guide-run.netlify.app","https://www.guiderun.org", "http://localhost:3000"},
        maxAge = 3600)
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

    @Operation(summary = "이벤트 검색 목록 조회", description = "이벤트 검색 화면에서 키워드 및 탭 조건으로 이벤트 목록을 페이지 조회합니다.")
    @GetMapping("/search")
    public SearchAllEventList searchAllEventList(
            @Parameter(description = "검색어", example = "상계천") @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @Parameter(description = "탭 구분", example = "UPCOMING") @RequestParam(value = "tab", defaultValue = "UPCOMING") String tab,
            @Parameter(description = "이벤트 유형 필터", example = "TOTAL") @RequestParam(value = "type", defaultValue = "TOTAL") EventType type,
            @Parameter(description = "모집 상태 필터", example = "RECRUIT_ALL") @RequestParam(value = "kind", defaultValue = "RECRUIT_ALL") EventRecruitStatus kind,
            @RequestParam(value = "cityName", required = false) CityName cityName,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") @RequestParam(value = "page", defaultValue = "0") int page,
            HttpServletRequest request) {
        validateParams(tab, type, kind);
        String privateId = extracted(request);
        int start = page * PAGE_SIZE;
        return eventSearchService.getSearchAllEvents(start, PAGE_SIZE, keyword, tab, type, kind, privateId, cityName);
    }

    @Operation(summary = "이벤트 검색 개수 조회", description = "이벤트 검색 화면에서 키워드 및 탭 조건 기준 검색 결과 개수를 조회합니다.")
    @GetMapping("/search/count")
    public SearchAllEventsCount searchAllEventCount(
            @Parameter(description = "검색어", example = "상계천") @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @Parameter(description = "탭 구분", example = "UPCOMING") @RequestParam(value = "tab", defaultValue = "UPCOMING") String tab,
            @Parameter(description = "이벤트 유형 필터", example = "TOTAL") @RequestParam(value = "type", defaultValue = "TOTAL") EventType type,
            @Parameter(description = "모집 상태 필터", example = "RECRUIT_ALL") @RequestParam(value = "kind", defaultValue = "RECRUIT_ALL") EventRecruitStatus kind,
            @RequestParam(value = "cityName", required = false) CityName cityName,
            HttpServletRequest request) {
        validateParams(tab, type, kind);
        String privateId = extracted(request);
        return eventSearchService.getSearchAllEventsCount(keyword, tab, type, kind, privateId, cityName);
    }

    private void validateParams(String tab, EventType type, EventRecruitStatus kind) {
        if (!tab.equals("UPCOMING") && !tab.equals("END") && !tab.equals("MY")) throw new NotValidSortException();
        if (!type.equals(TRAINING) && !type.equals(COMPETITION) && !type.equals(TOTAL)) throw new NotValidTypeException();
        if (!kind.equals(RECRUIT_UPCOMING) && !kind.equals(RECRUIT_OPEN) && !kind.equals(RECRUIT_CLOSE)
                && !kind.equals(RECRUIT_END) && !kind.equals(RECRUIT_ALL)) throw new NotValidKindException();
    }

    private String extracted(HttpServletRequest request) {
        String privateId = jwtProvider.extractUserId(request);
        userRepository.findUserByPrivateId(privateId)
                .orElseThrow(() -> new NotExistUserException());
        return privateId;
    }
}
