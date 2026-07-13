package com.guide.run.user.dto;

import com.guide.run.user.entity.user.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "회원 기본 인적사항")
public class PersonalInfoDto {
    @Schema(description = "권한", example = "USER")
    private String role;
    @Schema(description = "사용자 유형", example = "GUIDE")
    private String type;
    @Schema(description = "성별", example = "MALE")
    private String gender;
    @Schema(description = "이름", example = "홍길동")
    private String name;
    @Schema(description = "휴대전화 번호", example = "01012345678")
    private String phoneNumber;
    private Boolean isOpenNumber;
    private int age;
    private String snsId;
    private Boolean isOpenSns;
    private String id1365;
    private String birth;

    public static PersonalInfoDto userToInfoDto(User user){
        return PersonalInfoDto.builder()
                .role(user.getRole().getValue().substring(5))
                .type(user.getType().getValue())
                .name(user.getName())
                .gender(user.getGender())
                .phoneNumber(user.getPhoneNumber())
                .age(user.getAge())
                .snsId(user.getSnsId())
                .isOpenSns(user.getIsOpenSns())
                .isOpenNumber(user.getIsOpenNumber())
                .id1365(user.getId1365())
                .birth(user.getBirth())
                .build();
    }
}
