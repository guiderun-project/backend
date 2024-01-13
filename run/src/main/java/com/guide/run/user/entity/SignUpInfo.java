package com.guide.run.user.entity;

import jakarta.persistence.*;

@Entity
public class SignUpInfo {
    @Id
    private String userId;
    @Column(unique = true, nullable = false)
    private String accountId;
    private String password;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    private User user;
}
