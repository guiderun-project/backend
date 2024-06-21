package com.guide.run.global.scheduler.dto;

import com.guide.run.user.entity.type.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttendAndPartnerDto {
    private String privateId;
    private String partnerId;
    private UserType userType;
    private boolean isAttend;
    private List<Long> trainingIds;
    private List<Long> contestIds;

    public AttendAndPartnerDto(String privateId,
                               UserType type,
                               String viId,
                               String guideId,
                               boolean isAttend,
                               List<Long> trainingIds,
                               List<Long> contestIds) {
        this.privateId = privateId;

        if(type.equals(UserType.VI)){
            this.partnerId = guideId;
        }else{
            this.partnerId = viId;
        }
        this.userType = type;
        this.isAttend = isAttend;
        this.trainingIds = trainingIds;
        this.contestIds = contestIds;
    }
}
