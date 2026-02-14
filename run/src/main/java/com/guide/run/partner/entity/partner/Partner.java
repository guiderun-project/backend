package com.guide.run.partner.entity.partner;

import com.guide.run.global.converter.SetStringConverter;
import com.guide.run.global.entity.BaseEntity;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
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

    @Convert(converter = SetStringConverter.class)
    @Builder.Default
    private Set<Long> contestIds = new HashSet<>();
    @Convert(converter = SetStringConverter.class)
    @Builder.Default
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

    public void resetTraining(){
        this.trainingIds = new HashSet<>();
    }

    public void resetContest(){
        this.contestIds = new HashSet<>();
    }

}
