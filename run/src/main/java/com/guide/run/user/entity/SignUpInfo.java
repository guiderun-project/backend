package com.guide.run.user.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
public class SignUpInfo {
    @Id
    private String userId;
    @Column(unique = true)
    private String accountId;
    private String password;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(unique = true, name = "user_id")
    private User user;
}
