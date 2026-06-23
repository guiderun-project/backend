package com.guide.run.event.entity.dto.response.match;

import com.guide.run.user.entity.type.UserType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MatchingCompletedFlatDto {
    private String viUserId;
    private UserType viType;
    private String viName;
    private String viApplyRecord;
    private Boolean viIsAttended;
    private String viRecordDegree;
    private String viRunningGroup;
    private String guideUserId;
    private UserType guideType;
    private String guideName;
    private String guideApplyRecord;
    private Boolean guideIsAttended;
    private String guideRecordDegree;
}
