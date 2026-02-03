package com.guide.run.partner.entity.matching;

import com.guide.run.partner.entity.partner.PartnerId;
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
@IdClass(MatchingId.class)
@Table(indexes = {
    @Index(name = "idx_matching_event_id", columnList = "eventId"),
    @Index(name = "idx_matching_vi_id", columnList = "viId"),
    @Index(name = "idx_matching_event_vi", columnList = "eventId, viId")
})
public class Matching {
    @Id
    private long eventId;
    @Id
    private String guideId;

    private String guideRecord;
    private String viRecord;
    private String viId;

}
