package com.guide.run.user.entity;

import com.guide.run.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class User extends BaseEntity {
    @Id
    private String socialId;
    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder
    public User(String socialId, Role role) {
        this.socialId = socialId;
        this.role = role;
    }

}
