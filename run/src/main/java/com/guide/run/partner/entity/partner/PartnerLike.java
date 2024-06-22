package com.guide.run.partner.entity.partner;

import com.guide.run.global.converter.StringListConverter;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
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
@IdClass(PartnerLikeId.class)
public class PartnerLike {
    @Id
    private String recId; //받은 사람 id
    @Id
    private String sendId; //보낸 사람 id

}
