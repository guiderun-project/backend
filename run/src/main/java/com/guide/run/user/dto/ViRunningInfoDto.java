package com.guide.run.user.dto;

import com.guide.run.user.entity.ArchiveData;
import com.guide.run.user.entity.User;
import com.guide.run.user.entity.Vi;
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
    private boolean runningExp;
    private List<String> howToKnow = new ArrayList<>(); //러닝 경험 없을 시 null
    private String motive; //러닝 경험 없을 시 null
    private String hopePrefs;

    public ViRunningInfoDto fromEntity (User user, Vi vi, ArchiveData archiveData){

        return new ViRunningInfoDto(
                user.getRecordDegree(),
                user.getDetailRecord(),
                vi.isRunningExp(),
                archiveData.getHowToKnow(),
                archiveData.getMotive(),
                archiveData.getHopePrefs()
        );
    }
}
