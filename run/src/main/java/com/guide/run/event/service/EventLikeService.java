package com.guide.run.event.service;

import com.guide.run.event.entity.CommentLike;
import com.guide.run.event.entity.EventLike;
import com.guide.run.event.entity.dto.response.LikeCountResponse;
import com.guide.run.event.entity.repository.CommentLikeRepository;
import com.guide.run.event.entity.repository.EventCommentRepository;
import com.guide.run.event.entity.repository.EventLikeRepository;
import com.guide.run.global.exception.event.resource.NotExistCommentException;
import com.guide.run.global.exception.event.resource.NotExistEventException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventLikeService {
    private final CommentLikeRepository commentLikeRepository;
    private final EventLikeRepository eventLikeRepository;
    private final EventCommentRepository eventCommentRepository;

    @Transactional
    public LikeCountResponse pressCommentLike(Long commentId, String privateId) {
        CommentLike commentLike = commentLikeRepository.findById(commentId).orElseThrow(NotExistCommentException::new);
        List<String> privateIds = commentLike.getPrivateIds();
        if(privateIds.contains(privateId))
            privateIds.remove(privateId);
        else
            privateIds.add(privateId);
        commentLike.setPrivateIds(privateIds);
        commentLikeRepository.save(commentLike);
        return LikeCountResponse.builder().likes((long) privateIds.size()).build();
    }

    public LikeCountResponse getCommentLikeCount(Long commentId) {
        return LikeCountResponse.builder()
                .likes((long) commentLikeRepository
                        .findById(commentId)
                        .orElseThrow(NotExistCommentException::new)
                        .getPrivateIds()
                        .size()
                ).build();
    }

    @Transactional
    public LikeCountResponse pressEventLike(Long eventId, String privateId) {
        EventLike eventLike = eventLikeRepository.findById(eventId).orElseThrow(NotExistEventException::new);
        List<String> privateIds = eventLike.getPrivateIds();
        if(privateIds.contains(privateId))
            privateIds.remove(privateId);
        else
            privateIds.add(privateId);
        eventLike.setPrivateIds(privateIds);
        eventLikeRepository.save(eventLike);
        return LikeCountResponse.builder().likes((long) privateIds.size()).build();
    }

    public LikeCountResponse getEventLikeCount(Long eventId) {
        return LikeCountResponse.builder()
                .likes((long) eventLikeRepository
                        .findById(eventId)
                        .orElseThrow(NotExistEventException::new)
                        .getPrivateIds()
                        .size()
                ).build();
    }
}
