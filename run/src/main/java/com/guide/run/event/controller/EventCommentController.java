package com.guide.run.event.controller;

import com.guide.run.event.entity.dto.request.EventCommentCreateRequest;
import com.guide.run.event.entity.dto.response.comments.response.CommentsCreatedResponse;
import com.guide.run.event.entity.dto.response.comments.response.CommentsDeletedResponse;
import com.guide.run.event.entity.dto.response.comments.response.CommentsGetResponse;
import com.guide.run.event.entity.dto.response.get.Count;
import com.guide.run.event.service.EventCommentService;
import com.guide.run.global.jwt.JwtProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/event")
@Tag(name = "Event Comment", description = "이벤트 댓글 조회/생성/수정/삭제 API")
@SecurityRequirement(name = "bearerAuth")
public class EventCommentController {
    private final JwtProvider jwtProvider;
    private final EventCommentService eventCommentService;
    @Operation(summary = "이벤트 댓글 작성", description = "이벤트 상세 화면의 댓글 섹션에서 댓글을 작성합니다.")
    @PostMapping("/{eventId}/comments")
    public ResponseEntity<CommentsCreatedResponse> createComment(@PathVariable Long eventId,
                                                                 HttpServletRequest request,
                                                                 @RequestBody EventCommentCreateRequest eventCommentCreateRequest){
        String userId = jwtProvider.extractUserId(request);
        return ResponseEntity.status(200).body(CommentsCreatedResponse.builder()
                .commentId(eventCommentService.createComment(eventId,userId,eventCommentCreateRequest)).build());
    }
    @Operation(summary = "이벤트 댓글 삭제", description = "이벤트 상세 화면의 댓글 항목에서 댓글을 삭제합니다.")
    @DeleteMapping("/{eventId}/{commentId}")
    public ResponseEntity<CommentsDeletedResponse> deleteComment(@PathVariable Long eventId,
                                                                 @PathVariable Long commentId,
                                                                 HttpServletRequest request){
        String userId = jwtProvider.extractUserId(request);
        return ResponseEntity.status(200).body(CommentsDeletedResponse.builder()
                .commentId(eventCommentService.deleteComment(eventId,commentId,userId)).build());
    }
    @Operation(summary = "이벤트 댓글 수정", description = "이벤트 상세 화면의 댓글 항목에서 댓글 내용을 수정합니다.")
    @PatchMapping("/{eventId}/{commentId}")
    public ResponseEntity<CommentsCreatedResponse> patchComment(@PathVariable Long eventId,
                                                                 @PathVariable Long commentId,
                                                                @RequestBody EventCommentCreateRequest eventCommentCreateRequest,
                                                                 HttpServletRequest request){
        String userId = jwtProvider.extractUserId(request);
        return ResponseEntity.status(200).body(CommentsCreatedResponse.builder()
                .commentId(eventCommentService.patchComment(eventId,commentId,eventCommentCreateRequest,userId)).build());
    }
    @Operation(summary = "이벤트 댓글 목록 조회", description = "이벤트 상세 화면의 댓글 섹션에서 페이지네이션 댓글 목록을 조회합니다.")
    @GetMapping("/{eventId}/comments")
    public ResponseEntity<CommentsGetResponse> getComments(@PathVariable Long eventId,
                                                           @Parameter(description = "조회 개수", example = "4")
                                                           @RequestParam int limit,
                                                           @Parameter(description = "페이지 시작 offset", example = "0")
                                                           @RequestParam int start,
                                                           HttpServletRequest request){
        String userId = jwtProvider.extractUserId(request);
        return ResponseEntity.status(200).body(CommentsGetResponse.builder()
                .comments(eventCommentService.getComments(eventId,limit,start,userId)).build());
    }
    @Operation(summary = "이벤트 댓글 개수 조회", description = "이벤트 상세 화면에서 댓글 총 개수를 조회합니다.")
    @GetMapping("/{eventId}/comments/count")
    public ResponseEntity<Count> getCommentsCount(@PathVariable Long eventId,
                                                  HttpServletRequest request){
        return ResponseEntity.status(200).body(Count.builder()
                .count(eventCommentService.getCommentsCount(eventId)).build());
    }
}
