package com.guide.run.partner.entity.partner;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PartnerId implements Serializable {
    private String viId;
    private String guideId;
}
