package com.guide.run.partner.entity.partner;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class PartnerLikeId implements Serializable {
    private String recId; //받은 사람 id
    private String sendId; //보낸 사람 id
}
