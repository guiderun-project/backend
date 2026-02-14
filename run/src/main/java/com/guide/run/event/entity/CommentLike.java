package com.guide.run.event.entity;

import com.guide.run.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(CommentLikeId.class)
public class CommentLike extends BaseEntity {
    @Id
    private Long commentId;
    @Id
    private String privateId;
}
