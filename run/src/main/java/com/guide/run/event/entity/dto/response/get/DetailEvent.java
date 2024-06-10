package com.guide.run.event.entity.dto.response.get;

import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.event.entity.type.EventStatus;
import com.guide.run.event.entity.type.EventType;
import com.guide.run.user.entity.type.UserType;
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
    private LocalDate date;
    private String startTime;
    private String endTime;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private String place;
    private long NumV;
    private long NumG;

    private String partner;
    private UserType partnerType;
    private String partnerPace;
    private String details;

    private boolean checkOrganizer;
    private boolean submit;
    private EventStatus status;

    public DetailEvent(Long eventId, EventType type, String name, EventRecruitStatus recruitStatus, String organizer, UserType organizerType, String organizerPace, LocalDateTime date, LocalDateTime startTime, LocalDateTime endTime, LocalDateTime created_at, LocalDateTime updated_at, String place, long numV, long numG, String partner, UserType partnerType, String partnerPace, String details, boolean checkOrganizer, boolean submit, EventStatus status) {
        this.eventId = eventId;
        this.type = type;
        this.name = name;
        this.recruitStatus = recruitStatus;
        this.organizer = organizer;
        this.organizerType = organizerType;
        this.organizerPace = organizerPace;
        this.date = date.toLocalDate();
        this.startTime = startTime.getHour()+":"+startTime.getMinute();
        this.endTime = endTime.getHour()+":"+endTime.getMinute();
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.place = place;
        this.NumV = numV;
        this.NumG = numG;
        this.partner = partner;
        this.partnerType = partnerType;
        this.partnerPace = partnerPace;
        this.details = details;
        this.checkOrganizer = checkOrganizer;
        this.submit = submit;
        this.status = status;
    }

    public DetailEvent(Long eventId, EventType type, String name, EventRecruitStatus recruitStatus, String organizer, UserType organizerType, String organizerPace, LocalDateTime date, LocalDateTime startTime, LocalDateTime endTime, LocalDateTime created_at, LocalDateTime updated_at, String place, long numV, long numG, String details, boolean checkOrganizer, boolean submit, EventStatus status) {
        this.eventId = eventId;
        this.type = type;
        this.name = name;
        this.recruitStatus = recruitStatus;
        this.organizer = organizer;
        this.organizerType = organizerType;
        this.organizerPace = organizerPace;
        this.date = date.toLocalDate();
        this.startTime = startTime.getHour()+":"+startTime.getMinute();
        this.endTime = endTime.getHour()+":"+endTime.getMinute();
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.place = place;
        NumV = numV;
        NumG = numG;
        this.details = details;
        this.checkOrganizer = checkOrganizer;
        this.submit = submit;
        this.status = status;
    }
}
