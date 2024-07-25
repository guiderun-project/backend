package com.guide.run.temp.member.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "member")
public class Member{
    @Id
    private String id;
    private String name;
    private String type;
    private String gender;
    private String phoneNumber;
    private int age;
    private String detailRecord;
    private String recordDegree;
}
