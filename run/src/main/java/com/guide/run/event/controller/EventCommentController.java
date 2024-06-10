package com.guide.run.event.controller;

import com.guide.run.event.entity.dto.request.EventCommentCreateRequest;
import com.guide.run.event.entity.dto.response.comments.response.CommentsCreatedResponse;
import com.guide.run.event.entity.dto.response.comments.response.CommentsDeletedResponse;
import com.guide.run.event.entity.dto.response.comments.response.CommentsGetResponse;
import com.guide.run.event.service.EventCommentService;
import com.guide.run.global.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/event")
public class EventCommentController {
    private final JwtProvider jwtProvider;
    private final EventCommentService eventCommentService;
    @PostMapping("/{eventId}/comments")
    public ResponseEntity<CommentsCreatedResponse> createComment(@PathVariable Long eventId,
                                                                 HttpServletRequest request,
                                                                 @RequestBody EventCommentCreateRequest eventCommentCreateRequest){
        String userId = jwtProvider.extractUserId(request);
        return ResponseEntity.status(200).body(CommentsCreatedResponse.builder()
                .commentId(eventCommentService.createComment(eventId,userId,eventCommentCreateRequest)).build());
    }
    @DeleteMapping("/{eventId}/{commentId}")
    public ResponseEntity<CommentsDeletedResponse> deleteComment(@PathVariable Long eventId,
                                                                 @PathVariable Long commentId,
                                                                 HttpServletRequest request){
        String userId = jwtProvider.extractUserId(request);
        return ResponseEntity.status(200).body(CommentsDeletedResponse.builder()
                .commentId(eventCommentService.deleteComment(eventId,commentId,userId)).build());
    }
    @PatchMapping("/{eventId}/{commentId}")
    public ResponseEntity<CommentsCreatedResponse> patchComment(@PathVariable Long eventId,
                                                                 @PathVariable Long commentId,
                                                                @RequestBody EventCommentCreateRequest eventCommentCreateRequest,
                                                                 HttpServletRequest request){
        String userId = jwtProvider.extractUserId(request);
        return ResponseEntity.status(200).body(CommentsCreatedResponse.builder()
                .commentId(eventCommentService.patchComment(eventId,commentId,eventCommentCreateRequest,userId)).build());
    }
    @GetMapping("/{eventId}/comments")
    public ResponseEntity<CommentsGetResponse> getComments(@PathVariable Long eventId,
                                                           @RequestParam int limit,
                                                           @RequestParam int start,
                                                           HttpServletRequest request){
        String userId = jwtProvider.extractUserId(request);
        return ResponseEntity.status(200).body(CommentsGetResponse.builder()
                .comments(eventCommentService.getComments(eventId,limit,start)).build());
    }
}
