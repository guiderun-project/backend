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

    @NotBlank(message = "아이디를 입력하세요")
    private String accountId;

    @Size(min = 8, message = "비밀번호는 최소 8글자 이상이어야 합니다.")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&*]).{8,}$",
            message = "비밀번호는 숫자, 영어, 특수문자를 모두 포함해야 합니다.")
    private String password;

    @NotBlank(message = "이름을 입력하세요")
    private String name;
    @NotBlank(message = "성별을 입력하세요")
    private String gender;

    @Pattern(regexp = "^01(?:0|1|[6-9])(?:\\s|-)?(?:\\d{3}|\\d{4})?(?:\\s|-)?\\d{4}$",
            message = "전화번호 형식 에러")
    private String phoneNumber;

    @NotNull(message = "전화번호 공개 여부 오류")
    private boolean openNumber;

    @PositiveOrZero(message = "나이 입력 오류")
    private int age;

    private String detailRecord;

    @NotBlank(message = "기록 입력 오류")
    private String recordDegree; //개인 기록

    private String snsId;
    private boolean openSns;

    //vi 전용 정보
    @NotNull(message = "러닝 경험 입력 오류")
    private boolean runningExp;
    private String guideName;

    //아카이브 데이터
    private String runningPlace;
    private List<String> howToKnow = new ArrayList<>(); //러닝 경험 있을 시 null
    private String motive; //러닝 경험 있을 시 null

    //약관동의
    @NotNull(message = "약관 동의 입력 오류")
    private boolean privacy;
    @NotNull(message = "약관 동의 입력 오류")
    private boolean portraitRights;
}
