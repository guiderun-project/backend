package com.guide.run.user.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GuideSignupDto {

    @NotBlank
    private String accountId;
    
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&*]).{8,}$")
    private String password;

    @NotBlank
    private String name;

    @NotBlank
    private String gender;

    @Pattern(regexp = "^01(?:0|1|[6-9])(?:\\s|-)?(?:\\d{3}|\\d{4})?(?:\\s|-)?\\d{4}$")
    private String phoneNumber;

    @NotNull
    private Boolean isOpenNumber;

    @PositiveOrZero
    private int age;
    private String detailRecord;

    @NotBlank
    private String recordDegree; //개인 기록
    private String snsId;
    private Boolean isOpenSns;

    //가이드 전용 정보
    @NotNull
    private Boolean isGuideExp;
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
    @NotNull
    private boolean privacy;
    @NotNull
    private boolean portraitRights;
}
