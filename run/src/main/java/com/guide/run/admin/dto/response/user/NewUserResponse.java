package com.guide.run.admin.dto.response.user;

import com.guide.run.user.entity.type.Role;
import com.guide.run.user.entity.type.UserType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NewUserResponse {
    private String userId;
    private String img;
    private String role;
    private UserType type;
    private String name;
    private int trainingCnt;
    private int contestCnt;
    private int like;

    public NewUserResponse(String userId,
                           String img,
                           Role role,
                           UserType type,
                           String name,
                           int trainingCnt,
                           int contestCnt,
                           int like) {
        this.userId = userId;
        this.img = img;
        this.role = role.getValue();
        this.type = type;
        this.name = name;
        this.trainingCnt = trainingCnt;
        this.contestCnt = contestCnt;
        this.like = like;

    }
}
