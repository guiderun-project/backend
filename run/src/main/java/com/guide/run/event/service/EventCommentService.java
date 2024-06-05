package com.guide.run.event.service;

import com.guide.run.event.entity.Comment;
import com.guide.run.event.entity.CommentLike;
import com.guide.run.event.entity.Event;
import com.guide.run.event.entity.dto.request.EventCommentCreateRequest;
import com.guide.run.event.entity.dto.response.comments.GetComment;
import com.guide.run.event.entity.repository.CommentLikeRepository;
import com.guide.run.event.entity.repository.EventCommentRepository;
import com.guide.run.event.entity.repository.EventRepository;
import com.guide.run.global.exception.event.authorize.NotEventCommentWriterException;
import com.guide.run.global.exception.event.resource.NotExistCommentException;
import com.guide.run.global.exception.event.resource.NotExistEventException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventCommentService {
    private final EventRepository eventRepository;
    private final EventCommentRepository eventCommentRepository;
    private final CommentLikeRepository commentLikeRepository;
    @Transactional
    public Long createComment(Long eventId, String privateId, EventCommentCreateRequest eventCommentCreateRequest){
        eventRepository.findById(eventId).orElseThrow(NotExistEventException::new);
        Comment comment = Comment.builder()
                .comment(eventCommentCreateRequest.getContent())
                .eventId(eventId)
                .privateId(privateId)
                .build();
        Comment saved = eventCommentRepository.save(comment);
        CommentLike commentLike = CommentLike.builder()
                .commentId(saved.getCommentId())
                .privateIds(new ArrayList<>())
                .build();
        commentLikeRepository.save(commentLike);
        return comment.getCommentId();
    }
    @Transactional
    public Long deleteComment(Long eventId, Long commentId,String userId) {
        eventRepository.findById(eventId).orElseThrow(NotExistEventException::new);
        Comment comment = eventCommentRepository.findById(commentId).orElseThrow(NotExistEventException::new);
        if(!comment.getPrivateId().equals(userId))
            throw new NotEventCommentWriterException();
        eventCommentRepository.delete(comment);
        commentLikeRepository.deleteById(commentId);
        return comment.getCommentId();
    }
    @Transactional
    public Long patchComment(Long eventId, Long commentId, EventCommentCreateRequest eventCommentCreateRequest, String userId) {
        eventRepository.findById(eventId).orElseThrow(NotExistEventException::new);
        Comment comment = eventCommentRepository.findById(commentId).orElseThrow(NotExistEventException::new);
        if(!comment.getPrivateId().equals(userId))
            throw new NotEventCommentWriterException();
        return eventCommentRepository.save(
                Comment.builder()
                        .commentId(commentId)
                        .privateId(userId)
                        .eventId(eventId)
                        .comment(eventCommentCreateRequest.getContent())
                        .build()
        ).getCommentId();
    }

    //좋아요 만들고 다시 건들기
    public List<GetComment> getComments(Long eventId, int limit, int start) {
        eventRepository.findById(eventId).orElseThrow(NotExistEventException::new);
        return null;
    }
}
