package com.guide.run.user.dto.response.UserInfoAll;

import com.guide.run.user.entity.type.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GuideInfoAllResponse {

    private String name;
    private UserType type;
    private String role;
    private String gender;
    private String phoneNumber;
    private String recordDegree;
    private int age;
    private String snsId;
    private Boolean isOpenNumber;
    private Boolean isOpenSns;

    private String detailRecord;
    private Boolean isGuideExp;
    private String viName;
    private String viRecord;
    private String viCount; //상세한 가이드 경험
    private String guidingPace; //가이드 가능한 페이스 그룹
    private String runningPlace;
    @Builder.Default
    private List<String> howToKnow = new ArrayList<>();
    private String motive;
    private String hopePrefs;

    //약관동의
    private boolean privacy;
    private boolean portraitRights;
}
