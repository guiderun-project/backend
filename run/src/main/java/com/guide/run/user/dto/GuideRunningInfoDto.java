package com.guide.run.user.dto;

import com.guide.run.user.entity.ArchiveData;
import com.guide.run.user.entity.user.Guide;
import com.guide.run.user.entity.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GuideRunningInfoDto {
    private String recordDegree;
    private String detailRecord;
    private Boolean isGuideExp;
    private String viName;
    private String viRecord;
    private String viCount; //상세한 가이드 경험
    private String guidingPace; //가이드 가능한 페이스 그룹
    private String runningPlace;
    @Builder.Default
    private List<String> howToKnow = new ArrayList<>();
    private String motive;
    private String hopePrefs;

    public GuideRunningInfoDto fromEntity (User user, Guide guide, ArchiveData archiveData){

        return new GuideRunningInfoDto(
                user.getRecordDegree(),
                user.getDetailRecord(),
                guide.getIsGuideExp(),
                guide.getViName(),
                guide.getViRecord(),
                guide.getViCount(),
                guide.getGuidingPace(),
                archiveData.getRunningPlace(),
                archiveData.getHowToKnow(),
                archiveData.getMotive(),
                archiveData.getHopePrefs()
        );
    }
}
