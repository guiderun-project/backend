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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String socialId;
    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder
    public User(Long id, String socialId, String email,Role role) {
        this.id = id;
        this.socialId = socialId;
        this.role = role;
    }

}
