package com.guide.run.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(PartnerId.class)
public class Partner {
    @Id
    @ManyToOne
    @JoinColumn(name="VI_ID")
    private Vi viId;
    @Id
    @ManyToOne
    @JoinColumn(name = "GUIDE_ID")
    private Guide guideId;
    private int competitionCnt;
    private int trainingCnt;
}
