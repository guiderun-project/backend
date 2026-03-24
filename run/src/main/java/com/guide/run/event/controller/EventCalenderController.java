package com.guide.run.event.controller;

import com.guide.run.event.entity.dto.response.calender.MyEventsOfDayOfCalendar;
import com.guide.run.event.entity.dto.response.calender.MyEventsOfMonthOfCalender;
import com.guide.run.event.service.EventCalenderService;
import com.guide.run.global.exception.event.logic.NotValidDayException;
import com.guide.run.global.exception.event.logic.NotValidMonthException;
import com.guide.run.global.exception.event.logic.NotValidYearException;
import com.guide.run.global.jwt.JwtProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/event/calendar")
@Tag(name = "Event", description = "이벤트 캘린더 조회 API")
@SecurityRequirement(name = "bearerAuth")
public class EventCalenderController {
    private final JwtProvider jwtProvider;
    private final EventCalenderService eventCalenderService;
    @Operation(summary = "월간 이벤트 캘린더 조회", description = "이벤트 캘린더 화면에서 특정 월의 일자별 이벤트 존재 여부를 조회합니다.")
    @GetMapping("")
    public MyEventsOfMonthOfCalender getMyEventsOfMonth(@Parameter(description = "조회 연도", example = "2026") @RequestParam("year")int year,
                                                        @Parameter(description = "조회 월", example = "3") @RequestParam("month") int month,
                                                        HttpServletRequest request){
        if(year<0)
            throw new NotValidYearException();
        else if(month>12 || month<1)
            throw new NotValidMonthException();
        String userId = jwtProvider.extractUserId(request);
        return eventCalenderService.getMyEventsOfMonth(year,month,userId);
    }
    @Operation(summary = "일간 이벤트 캘린더 상세 조회", description = "이벤트 캘린더 화면에서 특정 날짜에 해당하는 상세 이벤트 목록을 조회합니다.")
    @GetMapping("/detail")
    public MyEventsOfDayOfCalendar getMyEventsOfDay(@Parameter(description = "조회 연도", example = "2026") @RequestParam("year")int year,
                                                    @Parameter(description = "조회 월", example = "3") @RequestParam("month") int month,
                                                    @Parameter(description = "조회 일", example = "20") @RequestParam("day") int day,
                                                    HttpServletRequest request){
        if(year<0)
            throw new NotValidYearException();
        else if(month>12 || month<1)
            throw new NotValidMonthException();
        else if(day> YearMonth.of(year,month).atEndOfMonth().getDayOfMonth() || day<1)
            throw new NotValidDayException();
        String userId = jwtProvider.extractUserId(request);
        return eventCalenderService.getMyEventsOfDay(LocalDate.of(year,month,day),userId);
    }
}
