package com.guide.run.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArchiveData {
    @Id
    private String userId;
    private String runningPlace;
    private String howToKnow;
    private String motive;

}
