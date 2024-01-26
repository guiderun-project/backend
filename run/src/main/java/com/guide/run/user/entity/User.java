package com.guide.run.user.entity;


import com.guide.run.global.entity.BaseEntity;
import com.guide.run.user.entity.type.Role;
import com.guide.run.user.entity.type.UserType;
import com.nimbusds.openid.connect.sdk.claims.Gender;
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
    private String uuid; //todo : 이름 바꿔야 함..

    @Id
    private String userId;

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
}

