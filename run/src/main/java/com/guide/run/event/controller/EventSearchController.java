package com.guide.run.event.controller;

import com.guide.run.event.entity.dto.response.search.SearchAllEventsCount;
import com.guide.run.event.entity.dto.response.search.SearchAllEventList;
import com.guide.run.event.entity.repository.EventRepository;
import com.guide.run.event.service.EventSearchService;
import com.guide.run.event.service.EventService;
import com.guide.run.global.exception.user.resource.NotExistUserException;
import com.guide.run.global.jwt.JwtProvider;

import com.guide.run.user.repository.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = {"https://guide-run-qa.netlify.app", "https://guiderun.org",
        "https://guide-run.netlify.app","https://www.guiderun.org", "http://localhost:3000"},
        maxAge = 3600)
@RequiredArgsConstructor
@RequestMapping("/api/event")
public class EventSearchController {
    private final JwtProvider jwtProvider;
    private final EventSearchService eventSearchService;
    private final UserRepository userRepository;
    @GetMapping("/search")
    public SearchAllEventList searchAllEventList(@RequestParam("title") String title,
                                                 @RequestParam("limit") int limit,
                                                 @RequestParam("start") int start,
                                                 HttpServletRequest request){
        extracted(request);
        System.out.println("title = " + title);
        return SearchAllEventList.builder().
                items(eventSearchService.getSearchAllEvents(start,limit,title))
                .build();
    }

    @GetMapping("/search/count")
    public SearchAllEventsCount searchAllEventCount(@RequestParam("title") String title,
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
