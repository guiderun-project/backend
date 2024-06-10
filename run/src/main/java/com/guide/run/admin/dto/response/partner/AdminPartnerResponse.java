package com.guide.run.admin.dto.response.partner;

import com.guide.run.user.entity.type.Role;
import com.guide.run.user.entity.type.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class AdminPartnerResponse {
    private String userId;
    private String img;
    private String role;
    private UserType type;
    private String name;
    private String recordDegree;
    private int like;

    public AdminPartnerResponse(String userId,
                                String img,
                                Role role,
                                UserType type,
                                String name,
                                String recordDegree,
                                List<String> sendIds) {
        this.userId = userId;
        this.img = img;
        this.role = role.getValue();
        this.type = type;
        this.name = name;
        this.recordDegree = recordDegree;
        if(sendIds==null){
            this.like = 0;
        }else{
            this.like = sendIds.size();
        }
    }
}
