package com.guide.run.event.controller;

import com.guide.run.event.entity.dto.response.LikeCountResponse;
import com.guide.run.event.entity.dto.response.LikeResponse;
import com.guide.run.event.service.EventLikeService;
import com.guide.run.global.jwt.JwtProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/event")
@Tag(name = "Event Like", description = "이벤트와 댓글 좋아요 API")
@SecurityRequirement(name = "bearerAuth")
public class EventLikeController {
    private final JwtProvider jwtProvider;
    private final EventLikeService eventLikeService;
    @Operation(summary = "댓글 좋아요 토글", description = "이벤트 상세 댓글 목록에서 특정 댓글의 좋아요 상태를 토글합니다.")
    @PostMapping("/comment/{commentId}/likes")
    public ResponseEntity<LikeResponse> pressCommentLike(@PathVariable Long commentId, HttpServletRequest request){
        String privateId = jwtProvider.extractUserId(request);
        return ResponseEntity.ok().body(eventLikeService.pressCommentLike(commentId,privateId));
    }

    @Operation(summary = "댓글 좋아요 상태 조회", description = "이벤트 상세 댓글 목록에서 좋아요 수와 현재 사용자의 좋아요 여부를 조회합니다.")
    @GetMapping("/comment/{commentId}/likes/count")
    public ResponseEntity<LikeCountResponse> getCommentLikeCount(@PathVariable Long commentId, HttpServletRequest request){
        String privateId = jwtProvider.extractUserId(request);
        return ResponseEntity.ok().body(eventLikeService.getCommentLikeCount(commentId,privateId));
    }

    @Operation(summary = "이벤트 좋아요 토글", description = "이벤트 상세 화면에서 이벤트 좋아요 상태를 토글합니다.")
    @PostMapping("/{eventId}/likes")
    public ResponseEntity<LikeResponse> pressEventLike(@PathVariable Long eventId, HttpServletRequest request){
        String privateId = jwtProvider.extractUserId(request);
        return ResponseEntity.ok().body(eventLikeService.pressEventLike(eventId,privateId));
    }
    @Operation(summary = "이벤트 좋아요 상태 조회", description = "이벤트 상세 화면에서 좋아요 수와 현재 사용자의 좋아요 여부를 조회합니다.")
    @GetMapping("/{eventId}/likes/count")
    public ResponseEntity<LikeCountResponse> getEventLikeCount(@PathVariable Long eventId, HttpServletRequest request){
        String privateId = jwtProvider.extractUserId(request);
        return ResponseEntity.ok().body(eventLikeService.getEventLikeCount(eventId,privateId));
    }
}
