package com.guide.run.event.entity.dto.response.comments;

import com.guide.run.user.entity.type.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Schema(description = "이벤트 댓글 항목")
public class GetComment {
    @Schema(description = "댓글 ID", example = "301")
    private Long commentId;
    @Schema(description = "작성자 이름", example = "홍길동")
    private String name;
    @Schema(description = "작성자 사용자 ID", example = "guide_102")
    private String userId;
    private UserType type;
    @Schema(description = "댓글 내용", example = "이번 주 토요일에도 참여할게요.")
    private String content;
    private LocalDate createdAt;
    @Schema(description = "좋아요 수", example = "3")
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
