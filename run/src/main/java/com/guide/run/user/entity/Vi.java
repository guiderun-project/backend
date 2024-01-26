package com.guide.run.user.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Vi {
    @Id
    private String userId;
    private boolean runningExp;
    private String guideName;
}
