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
@AllArgsConstructor
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
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private String place;
    private int NumV;
    private int NumG;

    private String partner;
    private UserType partnerType;
    private String partnerPace;
    private String details;

    private boolean checkOrganizer;
    private boolean submit;
    private EventStatus status;
}
