package com.guide.run.user.dto;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ViSignupDto {
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

    //vi 전용 정보
    private boolean runningExp;
    private String guideName;

    //아카이브 데이터
    private String runningPlace;
    private String howToKnow; //러닝 경험 있을 시 null
    private String motive; //러닝 경험 있을 시 null

    //약관동의
    private boolean privacy;
    private boolean portraitRights;
}
