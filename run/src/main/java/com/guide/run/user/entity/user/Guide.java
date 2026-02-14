package com.guide.run.user.entity.user;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Guide{
    @Id
    private String privateId;
    private Boolean isGuideExp;
    private String viName;
    private String viRecord;
    private String viCount; //상세한 가이드 경험
    private String guidingPace; //가이드 가능한 페이스 그룹

    public void editGuideRunningInfo(
            Boolean guideExp,
            String viName,
            String viRecord,
            String viCount,
            String guidingPace
    ){
        this.isGuideExp = guideExp;
        this.viName = viName;
        this.viRecord = viRecord;
        this.viCount = viCount;
        this.guidingPace = guidingPace;
    }
}
