package com.guide.run.user.dto;

import com.guide.run.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GlobalUserInfoDto {
    private String role;
    private String userId;
    private String recordDegree;
    private String type;
    private String gender;
    private String name;
    private String phoneNumber;
    private boolean openNumber;
    private int age;
    private String snsId;
    private boolean openSns;

    public static GlobalUserInfoDto userToInfoDto(User user){
        return GlobalUserInfoDto.builder()
                .userId(user.getUserId())
                .recordDegree(user.getRecordDegree())
                .role(user.getRole().getValue())
                .type(user.getType().getValue())
                .name(user.getName())
                .gender(user.getGender())
                .phoneNumber(user.getPhoneNumber())
                .age(user.getAge())
                .snsId(user.getSnsId())
                .openSns(user.isOpenSns())
                .openNumber(user.isOpenNumber())
                .build();
    }
}
