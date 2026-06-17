package com.guide.run.event.entity.dto.response.match;

import com.guide.run.user.entity.type.UserType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MatchingWaitingFlatDto {
    private String userId;
    private String name;
    private UserType type;
    private String hopeTeam;
    private String hopePartner;
    private String referContent;
    private int trainingCnt;
    private int competitionCnt;
}
