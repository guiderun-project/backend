package com.guide.run.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class GuideRunningInfoDto {
    private String recordDegree;
    private String detailRecord;
    private boolean guideExp;
    private String viName;
    private String viRecord;
    private String viCount; //상세한 가이드 경험
    private String guidingPace; //가이드 가능한 페이스 그룹
    private String howToKnow;
    private String motive;
    private String hopePrefs;
}
