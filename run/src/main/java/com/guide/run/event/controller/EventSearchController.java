package com.guide.run.event.controller;

import com.guide.run.event.entity.dto.response.search.SearchAllEventsCount;
import com.guide.run.event.entity.dto.response.search.SearchAllEventList;
import com.guide.run.event.service.EventSearchService;
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
    @Operation(summary = "이벤트 검색 목록 조회", description = "이벤트 검색 화면에서 제목 기준으로 이벤트 목록을 페이지네이션 조회합니다.")
    @GetMapping("/search")
    public SearchAllEventList searchAllEventList(@Parameter(description = "검색어", example = "상계천") @RequestParam("title") String title,
                                                 @Parameter(description = "페이지 크기", example = "10") @RequestParam("limit") int limit,
                                                 @Parameter(description = "페이지 시작 offset", example = "0") @RequestParam("start") int start,
                                                 HttpServletRequest request){
        extracted(request);
        System.out.println("title = " + title);
        return SearchAllEventList.builder().
                items(eventSearchService.getSearchAllEvents(start,limit,title))
                .build();
    }

    @Operation(summary = "이벤트 검색 개수 조회", description = "이벤트 검색 화면에서 제목 기준 검색 결과 개수를 조회합니다.")
    @GetMapping("/search/count")
    public SearchAllEventsCount searchAllEventCount(@Parameter(description = "검색어", example = "상계천") @RequestParam("title") String title,
                                                    HttpServletRequest request){
        extracted(request);
        return eventSearchService.getSearchAllEventsCount(title);
    }

    private String extracted(HttpServletRequest request) {
        String privateId = jwtProvider.extractUserId(request);
        userRepository.findUserByPrivateId(privateId).
                orElseThrow(() -> new NotExistUserException());
        return privateId;
    }

}
