package com.guide.run.admin.dto.response.user;

import com.guide.run.user.entity.type.UserType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
}
