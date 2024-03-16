package com.guide.run.user.entity.user;

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
    private String privateId;
    private Boolean isRunningExp;
    private String guideName;

    public void editViRunningInfo(Boolean runningExp, String guideName){
        this.isRunningExp = runningExp;
        this.guideName = guideName;
    }
}
