package com.guide.run.user.dto;

import com.guide.run.user.entity.type.UserType;
import com.guide.run.user.entity.user.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "현재 로그인 사용자 기본 정보")
public class GlobalUserInfoDto {
    @Schema(description = "공개 사용자 ID", example = "guide_102")
    private String userId;
    @Schema(description = "이름", example = "홍길동")
    private String name;
    @Schema(description = "사용자 유형", example = "GUIDE")
    private UserType type;
    @Schema(description = "권한", example = "USER")
    private String role;
    @Schema(description = "성별", example = "MALE")
    private String gender;
    @Schema(description = "휴대전화 번호", example = "01012345678")
    private String phoneNumber;
    @Schema(description = "러닝 등급", example = "A")
    private String recordDegree;
    private int age;
    private String snsId;
    private Boolean isOpenNumber;
    private Boolean isOpenSns;
    private String img; //2차 이미지 링크 추가
    private String id1365; //1365 아이디
    private String birth;

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
