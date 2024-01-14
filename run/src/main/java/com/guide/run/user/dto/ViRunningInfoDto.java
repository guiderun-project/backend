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
    private String howToKnow; //러닝 경험 없을 시 null
    private String motive; //러닝 경험 없을 시 null
    private String hopePrefs;
}
