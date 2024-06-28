package com.guide.run.global.scheduler.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long eventId;

    private LocalDateTime eventStart;
    private LocalDateTime eventEnd;

    private LocalDate recruitStart;
    private LocalDate recruitEnd;

    @Enumerated(EnumType.STRING)
    private ScheduleStatus eventStatus;

    @Enumerated(EnumType.STRING)
    private ScheduleStatus recruitStatus;

    public void changeTime(LocalDateTime startTime,
                           LocalDateTime endTime,
                           LocalDate recruitStart,
                           LocalDate recruitEnd){
        this.eventStart= startTime;
        this.eventEnd = endTime;
        this.recruitStart = recruitStart;
        this.recruitEnd = recruitEnd;
        this.eventStatus = ScheduleStatus.PENDING;
        this.recruitStatus = ScheduleStatus.PENDING;
    }

    public void changeEventStatus(ScheduleStatus status){
        this.eventStatus = status;
    }

    public void changeRecruitStatus(ScheduleStatus status){
        this.recruitStatus = status;
    }

}
