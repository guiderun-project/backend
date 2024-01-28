package com.guide.run.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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
    private boolean openSns;

    //vi 전용 정보
    private boolean runningExp;
    private String guideName;

    //아카이브 데이터
    private String runningPlace;
    private List<String> howToKnow = new ArrayList<>(); //러닝 경험 있을 시 null
    private String motive; //러닝 경험 있을 시 null

    //약관동의
    private boolean privacy;
    private boolean portraitRights;
}
