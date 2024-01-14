package com.guide.run.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ViRunningInfoDto {
    private String recordDegree;
    private String detailRecord;
    private boolean runningExp;
    private String howToKnow;
    private String motive;
    private String hopePrefs;
}
