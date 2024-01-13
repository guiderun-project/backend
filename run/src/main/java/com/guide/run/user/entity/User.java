package com.guide.run.user.entity;


import com.guide.run.global.entity.BaseEntity;
import com.guide.run.user.entity.type.Role;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@Entity
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn
@SuperBuilder
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
    private boolean openSNS;
}
