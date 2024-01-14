package com.guide.run.user.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class ArchiveData {
    @Id
    private String userId;
    private String runningPlace;
    private String howToKnow;
    private String motive;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(unique = true, name = "user_id")
    private User user;
}
