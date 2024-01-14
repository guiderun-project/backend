package com.guide.run.user.dto;

import com.guide.run.user.entity.type.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class PersonalInfoDto {
    private Role role;
    private String gender;
    private String name;
    private String phoneNumber;
    private int age;
    private String snsId;
}
