package com.guide.run.user.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@DiscriminatorValue("V")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Vi extends User{
    private boolean runningExp;
    private String guideName;
}
