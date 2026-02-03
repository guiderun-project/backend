package com.guide.run.partner.entity.partner;

import com.guide.run.global.converter.StringListConverter;
import jakarta.persistence.*;
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
@Table(indexes = {
    @Index(name = "idx_partner_like_rec_id", columnList = "recId")
})
public class PartnerLike {
    @Id
    private String recId; //받은 사람 id
    @Id
    private String sendId; //보낸 사람 id

}
