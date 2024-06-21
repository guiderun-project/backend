package com.guide.run.partner.entity.partner;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PartnerLikeId implements Serializable {
    private String recId; //받은 사람 id
    private String sendId; //보낸 사람 id
}
