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
public class ViSignupDto {

    @NotBlank
    private String accountId;

    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&*_]).{8,}$")
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

    private String recordDegree; //개인 기록

    private String snsId;
    private Boolean isOpenSns;

    //vi 전용 정보
    @NotNull
    private Boolean isRunningExp;
    private String guideName;

    private String hopePrefs;

    //아카이브 데이터
    private String runningPlace;
    private List<String> howToKnow = new ArrayList<>(); //러닝 경험 있을 시 null
    private String motive; //러닝 경험 있을 시 null

    //약관동의
    @NotNull
    private boolean privacy;
    @NotNull
    private boolean portraitRights;
}
