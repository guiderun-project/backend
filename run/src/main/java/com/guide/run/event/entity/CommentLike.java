package com.guide.run.event.entity;

import com.guide.run.global.converter.StringListConverter;
import com.guide.run.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentLike extends BaseEntity {
    @Id
    private Long commentId;
    @Convert(converter = StringListConverter.class)
    private List<String> privateIds;

    public void setPrivateIds(List<String> privateIds) {
        this.privateIds = privateIds;
    }
}
