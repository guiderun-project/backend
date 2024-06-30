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
public class Matching {
    @Id
    private long eventId;
    @Id
    private String guideId;

    private String guideRecord;
    private String viRecord;
    private String viId;

}
