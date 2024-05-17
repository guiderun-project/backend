package com.guide.run.partner.entity.matching;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(UnMatchingId.class)
public class UnMatching {
    @Id
    @JoinColumn(name = "EVENT_ID")
    private long eventId;
    @Id
    @JoinColumn(name = "USER_ID")
    private String privateId;
}
