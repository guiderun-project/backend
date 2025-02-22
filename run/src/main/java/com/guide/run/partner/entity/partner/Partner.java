package com.guide.run.partner.entity.partner;

import com.guide.run.global.converter.LongListConverter;
import com.guide.run.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(PartnerId.class)
public class Partner extends BaseEntity {
    @Id
    private String viId;
    @Id
    private String guideId;

    @Convert(converter = LongListConverter.class)
    private Set<Long> contestIds = new HashSet<>();
    @Convert(converter = LongListConverter.class)
    private Set<Long> trainingIds = new HashSet<>();

    public void addContest(Long eventId){
        this.contestIds.add(eventId);
    }

    public void addTraining(Long eventId){
        this.trainingIds.add(eventId);
    }

    public void removeContest(Long eventId){
        this.contestIds.remove(eventId);
    }
    public void removeTraining(Long eventId){
        this.trainingIds.remove(eventId);
    }

}
