package com.guide.run.event.controller;


import com.guide.run.event.entity.dto.response.get.Count;
import com.guide.run.event.entity.dto.response.get.MyEventResponse;
import com.guide.run.event.entity.repository.EventRepository;
import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.event.entity.type.EventType;
import com.guide.run.event.service.AllEventGetService;
import com.guide.run.event.service.EventGetService;
import com.guide.run.global.exception.event.logic.NotValidKindException;
import com.guide.run.global.exception.event.logic.NotValidSortException;
import com.guide.run.global.exception.event.logic.NotValidTypeException;
import com.guide.run.global.exception.event.logic.NotValidYearException;
import com.guide.run.global.jwt.JwtProvider;
import com.guide.run.user.repository.user.UserRepository;
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
public class EventGetController {
    private final JwtProvider jwtProvider;
    private final EventGetService eventGetService;

    @GetMapping("/my")
    public ResponseEntity<MyEventResponse> getMyEventList(@RequestParam("sort") String sort
    , @RequestParam("year") int year, HttpServletRequest request)
    {
        if(year<0){
            throw new NotValidYearException();
        }
        String userId = jwtProvider.extractUserId(request);
        MyEventResponse myEvent = eventGetService.getMyEvent(sort,year,userId);
        return ResponseEntity.status(200).body(myEvent);
    }
    @GetMapping("/all/count")
    public ResponseEntity<Count> getAllEventListCount(@RequestParam("sort") String sort,
                                                      @RequestParam("type") EventType type,
                                                      @RequestParam("kind") EventRecruitStatus kind,
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
                body(Count.builder().count(eventGetService.getAllEventListCount(sort,type,kind,userId)).build());
    }
}
