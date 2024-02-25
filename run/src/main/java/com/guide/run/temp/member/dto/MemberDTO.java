package com.guide.run.temp.member.dto;

import lombok.Data;

@Data
public class MemberDTO {
    private Long id;
    private String name;
    private String type;
    private String gender;
    private String phoneNumber;
    private int age;
    private String detailRecord;
    private String recordDegree;
}