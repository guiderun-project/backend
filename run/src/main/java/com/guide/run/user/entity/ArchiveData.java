package com.guide.run.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class ArchiveData {
    @Id
    private String userId;
    private String runningPlace;
    private String howToKnow;
    private String motive;
}
