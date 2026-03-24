package com.guide.run.admin.dto.response.user;

import com.guide.run.user.entity.type.Role;
import com.guide.run.user.entity.type.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "관리자 회원 목록 항목")
public class UserItem {
    @Schema(description = "공개 사용자 ID", example = "guide_102")
    private String userId;
    private String img;
    @Schema(description = "권한", example = "USER")
    private String role;
    private int age;
    @Schema(description = "사용자 유형", example = "GUIDE")
    private String type;
    @Schema(description = "이름", example = "홍길동")
    private String name;
    private String team;
    private String gender;
    private String snsId;
    private String phoneNumber;

    private int totalCnt;
    private int trainingCnt;
    private int competitionCnt;

    private String update_date;
    private String update_time;

    public UserItem(String userId,
                    String img,
                    Role role,
                    int age,
                    UserType type,
                    String name,
                    String team,
                    String gender,
                    String snsId,
                    String phoneNumber,
                    int trainingCnt,
                    int competitionCnt,
                    String update_date,
                    String update_time) {
        this.userId = userId;
        this.img = img;
        this.role = role.getValue().substring(5);
        this.age = age;
        this.type = type.getValue();
        this.name = name;
        this.team = team;
        this.gender = gender;
        this.snsId = snsId;
        this.phoneNumber = phoneNumber;
        this.totalCnt = trainingCnt+competitionCnt;
        this.trainingCnt = trainingCnt;
        this.competitionCnt = competitionCnt;
        this.update_date = update_date;
        this.update_time = update_time;
    }
}
