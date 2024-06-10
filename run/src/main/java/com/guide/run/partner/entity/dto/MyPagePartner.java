package com.guide.run.partner.entity.dto;

import com.guide.run.user.entity.type.Role;
import com.guide.run.user.entity.type.UserType;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MyPagePartner {
    private String userId;
    private String img;
    private String role;
    private String type;
    private String name;
    private String recordDegree;
    private int trainingCnt;
    private int contestCnt;
    private int like;
    private Boolean isLiked;

    public MyPagePartner(String userId,
                         String img,
                         Role role,
                         UserType type,
                         String name,
                         String recordDegree,
                         List<Long> trainingIds,
                         List<Long> contestIds,
                         List<String> sendIds,
                         String privateId ) {

        this.userId = userId;
        this.img = img;
        this.role = role.getValue();
        this.type = type.getValue();
        this.name = name;
        this.recordDegree = recordDegree;

        if(trainingIds==null){
            this.trainingCnt = 0;
        }else{
            this.trainingCnt = trainingIds.size();
        }
        if(contestIds==null){
            this.contestCnt = 0;
        }else{
            this.contestCnt = contestIds.size();
        }

        if(sendIds==null){
            this.like = 0;
            this.isLiked = false;
        }else{
            this.like = sendIds.size();
            this.isLiked = sendIds.contains(privateId);
        }
    }
}
