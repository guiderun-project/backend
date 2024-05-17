package com.guide.run.partner.entity.matching;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UnMatchingId implements Serializable {
    private long eventId;
    private String privateId;
}
