package com.guide.run.user.dto;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GuideSignupDto {
    //private String accountId;
    //private String password;
    private String name;
    private String gender;
    private String phoneNumber;
    private boolean openNumber;
    private int age;
    private String detailRecord;
    private String recordDegree; //개인 기록
    private String snsId;
    private boolean openSNS;

    //가이드 전용 정보
    private boolean guideExp;
    private String viName;
    private String viRecord;
    private String viCount; //상세한 가이드 경험
    private String guidingPace; //가이드 가능한 페이스 그룹

    //아카이브 데이터
    private String runningPlace;
    private String howToKnow; //가이드 경험 있을 시 null
    private String motive; //가이더 경험 있을 시 null
    private String hopePrefs;

    //약관동의
    private boolean privacy;
    private boolean portraitRights;
}
