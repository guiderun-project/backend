package com.guide.run.user.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
public class SignUpInfo {
    @Id
    private String signupId;
    @Column(unique = true)
    private String accountId;
    private String password;
}
