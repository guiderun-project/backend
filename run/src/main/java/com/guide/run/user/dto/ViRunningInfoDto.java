package com.guide.run.user.dto;

import com.guide.run.user.entity.ArchiveData;
import com.guide.run.user.entity.user.User;
import com.guide.run.user.entity.user.Vi;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "VI 사용자 러닝 스펙")
public class ViRunningInfoDto {
    @Schema(description = "현재 러닝 등급", example = "B")
    private String recordDegree;
    @Schema(description = "세부 기록", example = "10km 55분")
    private String detailRecord;
    @Schema(description = "러닝 경험 여부", example = "true")
    private Boolean isRunningExp;
    @Builder.Default
    private List<String> howToKnow = new ArrayList<>(); //러닝 경험 없을 시 null
    private String motive; //러닝 경험 없을 시 null
    private String runningPlace;
    private String guideName;
    private String hopePrefs;

    public ViRunningInfoDto fromEntity (User user, Vi vi, ArchiveData archiveData){

        return new ViRunningInfoDto(
                user.getRecordDegree(),
                user.getDetailRecord(),
                vi.getIsRunningExp(),
                archiveData.getHowToKnow(),
                archiveData.getMotive(),
                archiveData.getRunningPlace(),
                vi.getGuideName(),
                archiveData.getHopePrefs()
        );
    }
}
