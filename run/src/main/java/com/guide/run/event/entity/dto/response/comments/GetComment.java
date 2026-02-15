package com.guide.run.event.entity.dto.response.comments;

import com.guide.run.user.entity.type.UserType;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class GetComment {
    private Long commentId;
    private String name;
    private String userId;
    private UserType type;
    private String content;
    private LocalDate createdAt;
    private long likes;

    public GetComment(Long commentId, String name,String userId, UserType type, String content, LocalDateTime createdAt, Long likes) {
        this.commentId = commentId;
        this.name = name;
        this.userId = userId;
        this.type = type;
        this.content = content;
        this.createdAt = createdAt.toLocalDate();
        this.likes =likes;
    }
}
