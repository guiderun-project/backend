package com.guide.run.event.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentLikeId implements Serializable {
    private Long commentId;
    private String privateId;
}
