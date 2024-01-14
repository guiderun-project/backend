package com.guide.run.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
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

    private boolean guideExp;
    private String viName;
    private String viRecord;
    private String viCount; //상세한 가이드 경험
    private String guidingPace; //가이드 가능한 페이스 그룹
}
