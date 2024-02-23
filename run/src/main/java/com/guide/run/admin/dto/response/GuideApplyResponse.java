package com.guide.run.admin.dto.response;

import com.guide.run.global.converter.StringListConverter;
import jakarta.persistence.Convert;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GuideApplyResponse {

    private String phoneNumber;
    private int age;
    private String snsId;

    private boolean isGuideExp;
    private String recordDegree;
    private String detailRecord;
    private String viCount;
    private String guidingPace;
    private String runningPlace;

    private String hopePrefs;

    //가이드 경험 없을 시
    @Convert(converter = StringListConverter.class)
    private List<String> howToKnow = new ArrayList<>();
    private String motive;

    private boolean privacy;
    private boolean portraitRights;

}
