package com.guide.run.user.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GuideSignupDto {
    //private String accountId;
    //private String password;
    private String name;
    private String gender;
    @Pattern(regexp = "^01(?:0|1|[6-9])(?:\\s|-)?(?:\\d{3}|\\d{4})?(?:\\s|-)?\\d{4}$",
            message = "전화번호 형식이 올바르지 않습니다") //todo : 전화번호 형식 관련 에러코드 추가해야 함.
    private String phoneNumber;
    private boolean openNumber;
    private int age;
    private String detailRecord;
    private String recordDegree; //개인 기록
    private String snsId;
    private boolean openSns;

    //가이드 전용 정보
    private boolean guideExp;
    private String viName;
    private String viRecord;
    private String viCount; //상세한 가이드 경험
    private String guidingPace; //가이드 가능한 페이스 그룹

    //아카이브 데이터
    private String runningPlace;
    private List<String> howToKnow = new ArrayList<>(); //가이드 경험 있을 시 빈 배열
    private String motive; //가이더 경험 있을 시 null
    private String hopePrefs;

    //약관동의
    private boolean privacy;
    private boolean portraitRights;
}
