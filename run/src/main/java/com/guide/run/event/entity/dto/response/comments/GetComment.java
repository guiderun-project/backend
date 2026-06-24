package com.guide.run.event.entity.dto.response.comments;

import com.guide.run.user.entity.type.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Schema(description = "이벤트 댓글 항목")
public class GetComment {
    private static final DateTimeFormatter CREATED_AT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Schema(description = "댓글 ID", example = "301")
    private Long commentId;
    @Schema(description = "댓글 내용", example = "이번 주 토요일에도 참여할게요.")
    private String content;
    @Schema(description = "작성일시", example = "2024-06-15T13:45:30")
    private String createdAt;
    @Schema(description = "작성자 사용자 ID", example = "guide_102")
    private String userId;
    @Schema(description = "작성자 이름", example = "홍길동")
    private String name;
    private UserType type;

    public GetComment(Long commentId, String name, String userId, UserType type, String content, LocalDateTime createdAt) {
        this.commentId = commentId;
        this.content = content;
        this.createdAt = createdAt.format(CREATED_AT_FORMATTER);
        this.userId = userId;
        this.name = name;
        this.type = type;
    }
}
