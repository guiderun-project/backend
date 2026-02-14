package com.guide.run.partner.entity.dto;

import com.guide.run.user.entity.type.Role;
import com.guide.run.user.entity.type.UserType;
import lombok.*;

import java.util.Set;

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
                         Set<Long> trainingIds,
                         Set<Long> contestIds,
                         long like,
                         String sendId,
                         String privateId ) {

        this.userId = userId;
        this.img = img;
        this.role = role.getValue().substring(5);;
        this.type = type.getValue();
        this.name = name;
        this.recordDegree = recordDegree;
        this.like = (int)like;

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

        if(sendId!=null && sendId.equals(privateId)){
            this.isLiked = true;
        }else{
            this.isLiked = false;
        }
    }
}
