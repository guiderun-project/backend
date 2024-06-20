package com.guide.run.event.controller;

import com.guide.run.event.entity.dto.response.LikeCountResponse;
import com.guide.run.event.entity.dto.response.LikeResponse;
import com.guide.run.event.service.EventLikeService;
import com.guide.run.global.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/event")
public class EventLikeController {
    private final JwtProvider jwtProvider;
    private final EventLikeService eventLikeService;
    @PostMapping("/comment/{commentId}/likes")
    public ResponseEntity<LikeResponse> pressCommentLike(@PathVariable Long commentId, HttpServletRequest request){
        String privateId = jwtProvider.extractUserId(request);
        return ResponseEntity.ok().body(eventLikeService.pressCommentLike(commentId,privateId));
    }

    @GetMapping("/comment/{commentId}/likes/count")
    public ResponseEntity<LikeCountResponse> getCommentLikeCount(@PathVariable Long commentId, HttpServletRequest request){
        String privateId = jwtProvider.extractUserId(request);
        return ResponseEntity.ok().body(eventLikeService.getCommentLikeCount(commentId,privateId));
    }

    @PostMapping("/{eventId}/likes")
    public ResponseEntity<LikeResponse> pressEventLike(@PathVariable Long eventId, HttpServletRequest request){
        String privateId = jwtProvider.extractUserId(request);
        return ResponseEntity.ok().body(eventLikeService.pressEventLike(eventId,privateId));
    }
    @GetMapping("/{eventId}/likes/count")
    public ResponseEntity<LikeCountResponse> getEventLikeCount(@PathVariable Long eventId, HttpServletRequest request){
        String privateId = jwtProvider.extractUserId(request);
        return ResponseEntity.ok().body(eventLikeService.getEventLikeCount(eventId,privateId));
    }
}
