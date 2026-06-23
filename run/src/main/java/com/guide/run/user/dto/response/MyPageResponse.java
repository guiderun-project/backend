package com.guide.run.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "마이페이지 통합 조회 응답")
public class MyPageResponse {

    private Profile profile;
    private Participation participation;
    private PersonalInfo personalInfo;
    private RunningInfo runningInfo;

    @Getter
    @AllArgsConstructor
    @Builder
    @Schema(description = "프로필 정보")
    public static class Profile {
        @Schema(description = "이름", example = "홍길동")
        private String name;
        @Schema(description = "성별", example = "M")
        private String gender;
        @Schema(description = "사용자 유형", example = "VI")
        private String type;
        @Schema(description = "러닝 등급", example = "A")
        private String recordDegree;
    }

    @Getter
    @AllArgsConstructor
    @Builder
    @Schema(description = "참여 이력")
    public static class Participation {
        @Schema(description = "총 참여 횟수", example = "15")
        private int totalCount;
        @Schema(description = "대회 참여 횟수", example = "5")
        private int competitionCount;
        @Schema(description = "훈련 참여 횟수", example = "10")
        private int trainingCount;
    }

    @Getter
    @AllArgsConstructor
    @Builder
    @Schema(description = "개인 정보")
    public static class PersonalInfo {
        @Schema(description = "생년월일 (YYYY-MM-DD)", example = "1995-03-15", nullable = true)
        private String birthDate;
        @Schema(description = "전화번호", example = "01012345678", nullable = true)
        private String phoneNumber;
        @Schema(description = "SNS 아이디", example = "@runner", nullable = true)
        private String snsId;
        @Schema(description = "1365 아이디", example = "hong1365", nullable = true)
        private String id1365;
        @Schema(description = "로그인 계정 ID", example = "runner01", nullable = true)
        private String accountId;
    }

    @Getter
    @AllArgsConstructor
    @Builder
    @Schema(description = "러닝 정보")
    public static class RunningInfo {
        @Schema(description = "사용자 유형", example = "VI")
        private String type;
        @Schema(description = "러닝 등급", example = "A")
        private String recordDegree;
        @Schema(description = "세부 기록", example = "10km 55분", nullable = true)
        private String detailRecord;
        @Schema(description = "희망 훈련 지역/방식", example = "서울 한강", nullable = true)
        private String hopePrefs;
    }
}
