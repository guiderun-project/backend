package com.guide.run.user.entity;

import jakarta.persistence.*;

@Entity
public class ArchiveData {
    @Id
    private String userId;
    private String runningPlace;
    private String howToKnow;
    private String motive;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    private User user;
}
