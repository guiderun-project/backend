package com.guide.run.event.entity;

import com.guide.run.global.converter.StringListConverter;
import com.guide.run.global.entity.BaseEntity;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
public class EventLike extends BaseEntity {
    @Id
    private Long EventId;

    @Convert(converter = StringListConverter.class)
    private List<String> privateIds;

    public void setPrivateIds(List<String> privateIds) {
        this.privateIds = privateIds;
    }
}
