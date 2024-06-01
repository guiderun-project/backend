package com.guide.run.event.entity.dto.response.comments;

import com.guide.run.event.entity.type.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class GetComment {
    private Long commentId;
    private String name;
    private EventType type;
    private String content;
    private LocalDateTime createdAt;
    private Long likes;
}
