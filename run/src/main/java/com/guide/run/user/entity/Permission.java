package com.guide.run.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Permission {
    @Id
    private String userId;
    private boolean privacy;
    private boolean portraitRights;

}
