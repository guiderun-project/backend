package com.guide.run.user.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class User {
    @Id
    private String socialId;
    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder
    public User(String socialId, String email,Role role) {
        this.socialId = socialId;
        this.role = role;
    }

}
