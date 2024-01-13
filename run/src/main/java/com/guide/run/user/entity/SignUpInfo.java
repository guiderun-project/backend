package com.guide.run.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class SignUpInfo {
    @Id
    private String userId;
    @Column(unique = true, nullable = false)
    private String accountId;
    private String password;
}
