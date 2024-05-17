package com.guide.run.partner.entity.partner;

import com.guide.run.global.converter.LongListConverter;
import com.guide.run.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(PartnerId.class)
public class Partner extends BaseEntity {
    @Id
    @JoinColumn(name="VI_ID")
    private String viId;
    @Id
    @JoinColumn(name = "GUIDE_ID")
    private String guideId;

    private int contestCnt;
    private int trainingCnt;

    @Convert(converter = LongListConverter.class)
    private List<Long> eventIds = new ArrayList<>();

}
