package com.guide.run.partner.entity.partner;

import com.guide.run.global.converter.StringListConverter;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartnerLike {
    @Id
    private String recId; //받은 사람 id
    
    @Convert(converter = StringListConverter.class)
    private List<String> sendIds = new ArrayList<>(); //주는 사람 id 리스트

    public void editLike(List<String> sendIds){
        this.sendIds = sendIds;
    }
}
