package com.guide.run.user.dto.response;

import com.guide.run.user.entity.type.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileResponse {
    private String userId;
    private String role;
    private String name;
    private UserType type;
    private String gender;
    private String recordDegree;
    private String detailRecord;
    private String phoneNumber;
    private Boolean isOpenNumber;
    private int age;
    private String snsId;
    private Boolean isOpenSns;
    private int totalCnt;
    private int competitionCnt;
    private int trainingCnt;

    //2차 때 추가된 부분
    private String img;
    private Boolean isLiked;
    private int like;

}
