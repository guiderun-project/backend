package com.guide.run.event.service;

import com.guide.run.event.entity.CommentLike;
import com.guide.run.event.entity.EventLike;
import com.guide.run.event.entity.dto.response.LikeCountResponse;
import com.guide.run.event.entity.dto.response.LikeResponse;
import com.guide.run.event.entity.repository.CommentLikeRepository;
import com.guide.run.event.entity.repository.EventLikeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventLikeService {
    private final CommentLikeRepository commentLikeRepository;
    private final EventLikeRepository eventLikeRepository;

    @Transactional
    public LikeResponse pressCommentLike(Long commentId, String privateId) {
        Optional<CommentLike> commentLike = commentLikeRepository.findByCommentIdAndPrivateId(commentId, privateId);
        //좋아요 누른적 없음
        if(commentLike.isEmpty()){
            commentLikeRepository.save(CommentLike.builder().commentId(commentId).privateId(privateId).build());
        }
        //있음
        else{
            commentLikeRepository.delete(commentLike.get());
        }

        return LikeResponse.builder().likes(commentLikeRepository.countByCommentId(commentId)).build();
    }

    public LikeCountResponse getCommentLikeCount(Long commentId,String privateId) {
        Optional<CommentLike> commentLike = commentLikeRepository.findByCommentIdAndPrivateId(commentId, privateId);
        boolean isLiked;
        if(commentLike.isEmpty())
            isLiked=false;
        else
            isLiked=true;

        return LikeCountResponse.builder()
                .likes(commentLikeRepository.countByCommentId(commentId))
                .isLiked(isLiked)
                .build();
    }


    @Transactional
    public LikeResponse pressEventLike(Long eventId, String privateId) {
        Optional<EventLike> eventLike = eventLikeRepository.findByEventIdAndPrivateId(eventId, privateId);
        //이벤트 좋아요가 없으면
        if(eventLike.isEmpty()){
            eventLikeRepository.save(EventLike.builder().eventId(eventId).privateId(privateId).build());
        }
        //있으면
        else{
            eventLikeRepository.delete(eventLike.get());
        }
        return LikeResponse.builder().likes(eventLikeRepository.countByEventId(eventId)).build();
    }

    public LikeCountResponse getEventLikeCount(Long eventId,String privateId) {
        Optional<EventLike> eventLike = eventLikeRepository.findByEventIdAndPrivateId(eventId, privateId);
        boolean isLiked;
        if(eventLike.isEmpty())
            isLiked=false;
        else
            isLiked=true;
        return LikeCountResponse.builder()
                .likes(eventLikeRepository.countByEventId(eventId))
                .isLiked(isLiked)
                .build();
    }
}
