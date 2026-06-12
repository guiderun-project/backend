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

    @Operation(summary = "이벤트 검색 목록 조회", description = "이벤트 검색 화면에서 제목 및 필터 조건으로 이벤트 목록을 페이지네이션 조회합니다.")
    @GetMapping("/search")
    public SearchAllEventList searchAllEventList(
            @Parameter(description = "검색어", example = "상계천") @RequestParam("title") String title,
            @Parameter(description = "탭 구분", example = "UPCOMING") @RequestParam("sort") String sort,
            @Parameter(description = "이벤트 유형 필터", example = "TRAINING") @RequestParam("type") EventType type,
            @Parameter(description = "모집 상태 필터", example = "RECRUIT_OPEN") @RequestParam("kind") EventRecruitStatus kind,
            @RequestParam(value = "cityName", required = false) CityName cityName,
            @Parameter(description = "페이지 크기", example = "10") @RequestParam("limit") int limit,
            @Parameter(description = "페이지 시작 offset", example = "0") @RequestParam("start") int start,
          