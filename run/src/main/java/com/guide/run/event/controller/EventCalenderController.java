package com.guide.run.event.controller;

import com.guide.run.event.entity.dto.response.calender.MyEventsOfDayOfCalendar;
import com.guide.run.event.entity.dto.response.calender.MyEventsOfMonthOfCalender;
import com.guide.run.event.service.EventCalenderService;
import com.guide.run.global.exception.event.logic.NotValidDayException;
import com.guide.run.global.exception.event.logic.NotValidMonthException;
import com.guide.run.global.exception.event.logic.NotValidYearException;
import com.guide.run.global.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/event/calendar")
public class EventCalenderController {
    private final JwtProvider jwtProvider;
    private final EventCalenderService eventCalenderService;
    @GetMapping("")
    public MyEventsOfMonthOfCalender getMyEventsOfMonth(@RequestParam("year")int year,
                                                        @RequestParam("month") int month,
                                                        HttpServletRequest request){
        if(year<0)
            throw new NotValidYearException();
        else if(month>12 || month<1)
            throw new NotValidMonthException();
        String userId = jwtProvider.extractUserId(request);
        return eventCalenderService.getMyEventsOfMonth(year,month,userId);
    }
    @GetMapping("/detail")
    public MyEventsOfDayOfCalendar getMyEventsOfDay(@RequestParam("year")int year,
                                                    @RequestParam("month") int month,
                                                    @RequestParam("day") int day,
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
