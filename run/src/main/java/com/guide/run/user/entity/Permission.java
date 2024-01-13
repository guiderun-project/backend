package com.guide.run.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
}
