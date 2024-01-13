package com.guide.run.user.entity;


import com.guide.run.global.entity.BaseEntity;
import jakarta.persistence.*;
import jdk.jfr.Name;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn
@SuperBuilder
public class User extends BaseEntity {
    @Id
    @Column(name = "social_id")
    private String socialId;
    private String name;
    private String gender;
    private String phoneNumber;
    private int age;
    private String detailRecord;
    private String recordDegree; //개인 기록
    @Enumerated(EnumType.STRING)
    private Role role;
    //private List<Event> eventLists;
    private String snsId;
}
