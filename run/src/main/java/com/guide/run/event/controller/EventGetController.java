package com.guide.run.event.controller;


import com.guide.run.event.entity.dto.response.get.AllEventResponse;
import com.guide.run.event.entity.dto.response.get.Count;
import com.guide.run.event.entity.dto.response.get.MyEventResponse;
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
    @Operation(summary = "전체 이벤트 개수 조회", description = "전체 이벤트 탭에서 선택한 필터 조건에 맞는 총 이벤트 개수를 조회합니다.")
    @GetMapping("/all/count")
    public ResponseEntity<Count> getAllEventListCount(@Parameter(description = "탭 구분", example = "UPCOMING") @RequestParam("sort") String sort,
                                                      @Parameter(description = "이벤트 유형 필터", example = "TRAINING") @RequestParam("type") EventType type,
                                                      @Parameter(description = "모집 상태 필터", example = "RECRUIT_OPEN") @RequestParam("kind") EventRecruitStatus kind,
                                                      @RequestParam(value = "cityName", required = false) CityName cityName,
                                                      HttpServletRequest request){
        if(sort.equals("UPCOMING") || sort.equals("END") || sort.equals("MY")){}
        else throw new NotValidSortException();
        if(type.equals(TRAINING) || type.equals(COMPETITION) || type.equals(TOTAL)){}
        else throw new NotValidTypeException();
        if(kind.equals(RECRUIT_UPCOMING) || kind.equals(RECRUIT_OPEN) || kind.equals(RECRUIT_CLOSE)||
                kind.equals(RECRUIT_END)||kind.equals(RECRUIT_ALL)){}
        else throw new NotValidKindException();
        String userId = jwtProvider.extractUserId(request);
        return ResponseEntity.status(200).
                body(Count.builder().count(eventGetService.getAllEventListCount(sort,type,kind,userId,cityName)).build());
    }

    @Operation(summary = "전체 이벤트 목록 조회", description = "전체 이벤트 탭에서 선택한 필터와 페이지네이션 조건에 맞는 이벤트 목록을 조회합니다.")
    @GetMapping("/all")
    public ResponseEntity<AllEventResponse> getAllEventList(@Parameter(description = "탭 구분", example = "UPCOMING") @RequestParam("sort") String sort,
                                                            @Parameter(description = "이벤트 유형 필터", example = "TRAINING") @RequestParam("type") EventType type,
                                                            @Parameter(description = "모집 상태 필터", example = "RECRUIT_OPEN") @RequestParam("kind") EventRecruitStatus kind,
                                                            @RequestParam(value = "cityName", required = false) CityName cityName,
                                                            @Parameter(description = "페이지 크기", example = "6")
                                                            @RequestParam("limit") int limit,
                                                            @Parameter(description = "페이지 시작 offset", example = "0")
                                                            @RequestParam("start") int start,
                                                            HttpServletRequest request){
        if(sort.equals("UPCOMING") || sort.equals("END") || sort.equals("MY")){}
        else throw new NotValidSortException();
        if(type.equals(TRAINING) || type.equals(COMPETITION) || type.equals(TOTAL)){}
        else throw new NotValidTypeException();
        if(kind.equals(RECRUIT_UPCOMING) || kind.equals(RECRUIT_OPEN) || kind.equals(RECRUIT_CLOSE)||
                kind.equals(RECRUIT_END)||kind.equals(RECRUIT_ALL)){}
        else throw new NotValidKindException();
        String userId = jwtProvider.extractUserId(request);
        return ResponseEntity.status(200).
                body(eventGetService.getAllEventList(limit,start,sort,type,kind,userId,cityName));
    }
}
