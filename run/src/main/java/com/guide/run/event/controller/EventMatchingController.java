package com.guide.run.event.controller;

import com.guide.run.event.entity.dto.response.match.*;
import com.guide.run.event.service.EventMatchingService;
import com.guide.run.global.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = {"https://guide-run-qa.netlify.app", "https://guiderun.org",
        "https://guide-run.netlify.app","https://www.guiderun.org", "http://localhost:3000"},
        maxAge = 3600)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/event")
public class EventMatchingController {
    private final JwtProvider jwtProvider;
    private final EventMatchingService eventMatchingService;

    @PostMapping("{eventId}/match/{viId}/{userId}")
    public ResponseEntity<String> matchUser(@PathVariable("eventId") Long eventId,
                                            @PathVariable("viId") String viId,
                                            @PathVariable("userId") String userId,
                                            HttpServletRequest request){
        jwtProvider.extractUserId(request);
        eventMatchingService.matchUser(eventId,viId,userId);
        return ResponseEntity.ok().body("200 ok");
    }
    @DeleteMapping("{eventId}/match/{viId}/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable("eventId") Long eventId,
                                            @PathVariable("viId") String viId,
                                            @PathVariable("userId") String userId,
                                            HttpServletRequest request){
        jwtProvider.extractUserId(request);
        eventMatchingService.deleteMatchUser(eventId,viId,userId);
        return ResponseEntity.ok().body("200 ok");
    }
    @GetMapping("{eventId}/match/not/count")
    public ResponseEntity<UserTypeCount> getUserTypeCount(@PathVariable("eventId") Long eventId,
                                                          HttpServletRequest request){
        jwtProvider.extractUserId(request);
        return ResponseEntity.ok().body(eventMatchingService.getUserTypeCount(eventId));
    }
    @GetMapping("{eventId}/match/list")
    public ResponseEntity<NotMatchList> getNotMatchList(@PathVariable("eventId") Long eventId,
                                                         HttpServletRequest request){
        jwtProvider.extractUserId(request);
        return ResponseEntity.ok().body(NotMatchList.builder()
                .notMatch(eventMatchingService.getNotMatchList(eventId)).build());
    }
    @GetMapping("{eventId}/match/{viId}/count")
    public ResponseEntity<MatchedGuideCount> getMatchedGuideCount(@PathVariable("eventId") Long eventId,
                                                                  @PathVariable("viId") String viId,
                                                                  HttpServletRequest request){
        jwtProvider.extractUserId(request);
        return ResponseEntity.ok().body(eventMatchingService.getMatchedGuideCount(eventId,viId));
    }
    @GetMapping("{eventId}/match/{viId}/list")
    public ResponseEntity<MatchedGuideList> getMatchedGuideList(@PathVariable("eventId") Long eventId,
                                                                @PathVariable("viId") String viId,
                                                                HttpServletRequest request){
        jwtProvider.extractUserId(request);
        return ResponseEntity.ok().body(eventMatchingService.getMatchedGuideList(eventId,viId));
    }
    @GetMapping("{eventId}/match/vi/count")
    public ResponseEntity<MatchedViCount> getMatchedViCount(@PathVariable("eventId") Long eventId,
                                                               HttpServletRequest request){
        jwtProvider.extractUserId(request);
        return ResponseEntity.ok().body(eventMatchingService.getMatchedViCount(eventId));
    }
    @GetMapping("{eventId}/match/vi/list")
    public ResponseEntity<MatchedViList> getMatchedViList(@PathVariable("eventId") Long eventId,
                                                            HttpServletRequest request){
        jwtProvider.extractUserId(request);
        return ResponseEntity.ok().body(eventMatchingService.getMatchedViList(eventId));
    }

}
