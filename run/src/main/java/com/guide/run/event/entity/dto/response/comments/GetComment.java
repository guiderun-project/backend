package com.guide.run.event.entity.dto.response.comments;

import com.guide.run.event.entity.type.EventType;
import com.guide.run.user.entity.type.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class GetComment {
    private Long commentId;
    private String name;
    private UserType type;
    private String content;
    private LocalDateTime createdAt;
    private long likes;

    public GetComment(Long commentId, String name, UserType type, String content, LocalDateTime createdAt, List<String> likes) {
        this.commentId = commentId;
        this.name = name;
        this.type = type;
        this.content = content;
        this.createdAt = createdAt;
        this.likes =likes.size();
    }
}
