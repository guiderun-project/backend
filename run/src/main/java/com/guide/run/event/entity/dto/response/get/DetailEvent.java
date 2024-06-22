package com.guide.run.event.entity.dto.response.get;


import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.event.entity.type.EventStatus;
import com.guide.run.event.entity.type.EventType;
import com.guide.run.user.entity.type.UserType;
import com.guide.run.user.entity.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class DetailEvent {
    private Long eventId;
    private EventType type;
    private String name;
    private EventRecruitStatus recruitStatus;
    private String organizer;
    private UserType organizerType;
    private String organizerPace;
    private String date;
    private String startTime;
    private String endTime;
    private LocalDate created_at;
    private LocalDate updated_at;
    private String place;
    //모집인원
    private int minNumV;
    private int minNumG;
    //참여인원
    private int numV;
    private int numG;

    private String partner;
    private UserType partnerType;
    private String partnerPace;
    private String details;

    private boolean checkOrganizer;
    private boolean submit;
    private EventStatus status;

    public DetailEvent(Long eventId, EventType type, String name, EventRecruitStatus recruitStatus, String organizer, UserType organizerType, String organizerPace, String date, String startTime, String endTime, LocalDateTime created_at, LocalDateTime updated_at, String place, int numV, int numG, String partner, UserType partnerType, String partnerPace, String details, boolean checkOrganizer, boolean submit, EventStatus status) {
        this.eventId = eventId;
        this.type = type;
        this.name = name;
        this.recruitStatus = recruitStatus;
        this.organizer = organizer;
        this.organizerType = organizerType;
        this.organizerPace = organizerPace;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.created_at = created_at.toLocalDate();
        this.updated_at = updated_at.toLocalDate();
        this.place = place;
        NumV = numV;
        NumG = numG;
        this.partner = partner;
        this.partnerType = partnerType;
        this.partnerPace = partnerPace;
        this.details = details;
        this.checkOrganizer = checkOrganizer;
        this.submit = submit;
        this.status = status;
    }
}
