package com.guide.run.user.entity.user;


import com.guide.run.global.entity.BaseEntity;
import com.guide.run.user.entity.type.Role;
import com.guide.run.user.entity.type.UserType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class User extends BaseEntity {
    @Column(unique = true, nullable = false)
    private String userId; //uuid
    @Id
    private String privateId;

    private String name;

    private String gender;
    private String phoneNumber;
    private boolean openNumber;
    private int age;
    private String detailRecord;
    private String recordDegree; //개인 기록

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private UserType type;
    private String snsId;
    private boolean openSns;

    private int trainingCnt; //참여한 훈련 수
    private int competitionCnt; //참여한 대회 수
    
    public void editUser(String name,
                         String gender,
                         String phoneNumber,
                         boolean openNumber,
                         int age,
                         String snsId,
                         boolean openSns) {
        this.name = name;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.openNumber = openNumber;
        this.age = age;
        this.snsId = snsId;
        this.openSns = openSns;
    }

    public void editRunningInfo(String recordDegree, String detailRecord){
        this.recordDegree = recordDegree;
        this.detailRecord = detailRecord;
    }

    public void editUserRole(Role role){
        this.role = role;
    }
}

