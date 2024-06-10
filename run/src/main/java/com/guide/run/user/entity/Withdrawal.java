package com.guide.run.user.entity;

import com.guide.run.global.converter.StringListConverter;
import com.guide.run.global.entity.BaseEntity;
import com.guide.run.user.entity.type.Role;
import com.guide.run.user.entity.type.UserType;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class Withdrawal extends BaseEntity {
    @Id
    private String privateId;
    private String userId;

    private UserType type;
    private String name;
    private String recordDegree;
    private String gender;
    private Role role;

    @Convert(converter = StringListConverter.class)
    private List<String> deleteReasons = new ArrayList<>(); //탈퇴 사유
}
