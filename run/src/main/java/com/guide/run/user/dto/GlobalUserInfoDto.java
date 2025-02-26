package com.guide.run.user.dto;

import com.guide.run.user.entity.type.UserType;
import com.guide.run.user.entity.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GlobalUserInfoDto {
    private String userId;
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
    private String img; //2차 이미지 링크 추가
    private String id1365; //1365 아이디
    private LocalDate birth;

    public static GlobalUserInfoDto userToInfoDto(User user){
        return GlobalUserInfoDto.builder()
                .userId(user.getUserId())
                .recordDegree(user.getRecordDegree())
                .role(user.getRole().getValue().substring(5))
                .type(user.getType())
                .name(user.getName())
                .gender(user.getGender())
                .phoneNumber(user.getPhoneNumber())
                .age(user.getAge())
                .snsId(user.getSnsId())
                .isOpenSns(user.getIsOpenSns())
                .isOpenNumber(user.getIsOpenNumber())
                .img(user.getImg())
                .id1365(user.getId1365())
                .birth(user.getBirth())
                .build();
    }
}
