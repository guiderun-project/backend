package com.guide.run.user.dto.request;

import com.guide.run.user.entity.type.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 통합 회원가입 요청. VI/Guide 구분 없이 하나의 엔드포인트(POST /api/signup)로 받는다.
 * 계정 ID/비밀번호는 별도 API(POST /api/user/account)에서 설정하므로 여기서는 받지 않는다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "통합 회원가입 요청")
public class SignupRequest {

    @NotNull
    @Schema(description = "사용자 유형", example = "VI")
    private UserType disabilityType;

    @NotNull
    @Valid
    private Common common;

    @Valid
    private Vi vi;

    @Valid
    private Guide guide;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "공통 기본 정보")
    public static class Common {
        @NotBlank
        @Schema(description = "이름", example = "홍길동")
        private String name;

        @Schema(description = "생년월일 (YYYY-MM-DD)", example = "1990-01-01")
        private String birthDate;

        @Pattern(regexp = "^01(?:0|1|[6-9])(?:\\s|-)?(?:\\d{3}|\\d{4})?(?:\\s|-)?\\d{4}$")
        @Schema(description = "휴대전화 번호", example = "01012345678")
        private String phoneNumber;

        @Schema(description = "SNS ID", example = "@guiderun")
        private String snsId;

        @Schema(description = "성별", example = "MALE")
        private String gender;

        @Schema(description = "전화번호 공개 여부", example = "false")
        private Boolean isOpenNumber;

        @Schema(description = "SNS 공개 여부", example = "false")
        private Boolean isOpenSns;

        @Schema(description = "개인정보 수집 동의 (true 필수)", example = "true")
        private boolean privacy;

        @Schema(description = "초상권 활용 동의 (true 필수)", example = "true")
        private boolean portraitRights;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "VI 전용 정보")
    public static class Vi {
        @Schema(description = "러닝 그룹/등급", example = "A")
        private String runningGroup;
        @Schema(description = "상세 기록")
        private String detailRecord;
        @Schema(description = "희망 사항")
        private String hopePrefs;
        @Schema(description = "러닝 경험 여부", example = "true")
        private Boolean isRunningExp;
        @Schema(description = "함께한 가이드 이름")
        private String guideName;
        @Schema(description = "주 활동 장소")
        private String runningPlace;
        @Schema(description = "알게 된 경로")
        private List<String> howToKnow = new ArrayList<>();
        @Schema(description = "가입 동기")
        private String motive;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Guide 전용 정보")
    public static class Guide {
        @Schema(description = "러닝 그룹/등급", example = "A")
        private String runningGroup;
        @Schema(description = "상세 기록")
        private String detailRecord;
        @Schema(description = "희망 사항")
        private String hopePrefs;
        @Schema(description = "가이드 경험 여부", example = "true")
        private Boolean isGuideExp;
        @Schema(description = "함께한 VI 이름")
        private String viName;
        @Schema(description = "VI 기록")
        private String viRecord;
        @Schema(description = "가이드 경험 횟수")
        private String viCount;
        @Schema(description = "가이드 가능 페이스")
        private String guidingPace;
        @Schema(description = "주 활동 장소")
        private String runningPlace;
        @Schema(description = "알게 된 경로")
        private List<String> howToKnow = new ArrayList<>();
        @Schema(description = "가입 동기")
        private String motive;
    }
}
