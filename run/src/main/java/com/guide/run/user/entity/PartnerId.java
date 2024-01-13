package com.guide.run.user.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@AllArgsConstructor
@NoArgsConstructor
public class PartnerId implements Serializable {
    private Long viId;
    private Long guideId;
}
