package com.guide.run.user.dto.response;

import com.guide.run.user.entity.type.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "사용자 프로필 상세 응답")
public class ProfileResponse {
    @Schema(description = "공개 사용자 ID", example = "guide_102")
    private String userId;
    @Schema(description = "권한", example = "USER")
    private String role;
    @Schema(description = "이름", example = "홍길동")
    private String name;
    @Schema(description = "사용자 유형", example = "GUIDE")
    private UserType type;
    @Schema(description = "성별", example = "MALE")
    private String gender;
    @Schema(description = "러닝 등급", example = "A")
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
    @Schema(description = "현재 로그인 사용자의 좋아요 여부", example = "true")
    private Boolean isLiked;
    @Schema(description = "누적 좋아요 수", example = "12")
    private int like;

    private String id1365;
    private String birth;

}
