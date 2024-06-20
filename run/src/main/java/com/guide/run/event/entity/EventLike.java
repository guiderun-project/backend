package com.guide.run.event.entity;

import com.guide.run.global.entity.BaseEntity;
import com.guide.run.partner.entity.matching.MatchingId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
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
@IdClass(EventLikeId.class)
public class EventLike extends BaseEntity {
    @Id
    private Long eventId;
    @Id
    private String privateId;
}
