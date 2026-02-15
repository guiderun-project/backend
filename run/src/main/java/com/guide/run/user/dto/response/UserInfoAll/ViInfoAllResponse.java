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
public class ViInfoAllResponse {
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
    private Boolean isRunningExp;
    @Builder.Default
    private List<String> howToKnow = new ArrayList<>(); //러닝 경험이 없으면 빈 리스트
    private String motive; //러닝 경험 없을 시 null
    private String runningPlace;
    private String guideName;
    private String hopePrefs;


    //약관동의
    private boolean privacy;
    private boolean portraitRights;
}
