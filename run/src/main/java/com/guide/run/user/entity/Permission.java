package com.guide.run.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Permission {
    @Id
    private String userId;
    private Boolean privacy;
    private Boolean portraitRights;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    private User user;
}
