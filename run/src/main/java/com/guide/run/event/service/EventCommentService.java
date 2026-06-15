package com.guide.run.event.service;

import com.guide.run.event.entity.Comment;
import com.guide.run.event.entity.dto.request.EventCommentCreateRequest;
import com.guide.run.event.entity.dto.response.comments.GetComment;
import com.guide.run.event.entity.dto.response.comments.response.CommentsGetResponse;
import com.guide.run.event.entity.repository.EventCommentRepository;
import com.guide.run.event.entity.repository.EventRepository;
import com.guide.run.global.exception.event.authorize.NotEventCommentWriterException;
import com.guide.run.global.exception.event.resource.NotExistCommentException;
import com.guide.run.global.exception.event.resource.NotExistEventException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class EventCommentService {
    private final EventRepository eventRepository;
    private final EventCommentRepository eventCommentRepository;
    @Transactional
    public Long createComment(Long eventId, String privateId, EventCommentCreateRequest eventCommentCreateRequest){
        eventRepository.findById(eventId).orElseThrow(NotExistEventException::new);
        Comment comment = Comment.builder()
                .comment(eventCommentCreateRequest.getContent())
                .eventId(eventId)
                .privateId(privateId)
                .build();
        Comment saved = eventCommentRepository.save(comment);
        return saved.getCommentId();
    }
    @Transactional
    public Long deleteComment(Long eventId, Long commentId,String userId) {
        eventRepository.findById(eventId).orElseThrow(NotExistEventException::new);
        Comment comment = eventCommentRepository.findById(commentId).orElseThrow(NotExistCommentException::new);
        if(!comment.getPrivateId().equals(userId))
            throw new NotEventCommentWriterException();
        eventCommentRepository.delete(comment);

        return comment.getCommentId();
    }
    @Transactional
    public Long patchComment(Long eventId, Long commentId, EventCommentCreateRequest eventCommentCreateRequest, String userId) {
        eventRepository.findById(eventId).orElseThrow(NotExistEventException::new);
        Comment comment = eventCommentRepository.findById(commentId).orElseThrow(NotExistCommentException::new);
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

    public CommentsGetResponse getComments(Long eventId, int page, int size, String userId) {
        eventRepository.findById(eventId).orElseThrow(NotExistEventException::new);
        List<GetComment> items = eventCommentRepository.findGetComments(page, size, eventId, userId);
        long totalCount = eventCommentRepository.countByEventId(eventId);
        int totalPages = (int) Math.ceil((double) totalCount / size);
        return CommentsGetResponse.builder()
                .items(items)
                .page(CommentsGetResponse.Page.builder()
                        .page(page)
                        .size(size)
                        .totalCount(totalCount)
                        .totalPages(totalPages)
                        .build())
                .build();
    }

    public long getCommentsCount(Long eventId) {
        eventRepository.findById(eventId).orElseThrow(NotExistEventException::new);
        return eventCommentRepository.countByEventId(eventId);
    }
}
