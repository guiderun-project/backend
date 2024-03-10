package com.guide.run.user.dto;

import com.guide.run.user.entity.ArchiveData;
import com.guide.run.user.entity.user.User;
import com.guide.run.user.entity.user.Vi;
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
public class ViRunningInfoDto {
    private String recordDegree;
    private String detailRecord;
    private Boolean isRunningExp;
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
