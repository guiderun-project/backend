package com.guide.run.user.entity;


import com.guide.run.global.entity.BaseEntity;
import com.guide.run.user.entity.type.Role;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;


@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn
@SuperBuilder
public class User extends BaseEntity {
    @Column(unique = true, nullable = false)
    private String uuid;

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
    //private List<Event> eventLists;
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

