package com.guide.run.admin.dto.response.partner;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PartnerTypeResponse {
    private int contestCnt;
    private int trainingCnt;
}
