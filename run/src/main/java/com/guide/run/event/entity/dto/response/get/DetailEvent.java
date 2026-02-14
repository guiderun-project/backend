package com.guide.run.event.entity.dto.response.get;


import com.guide.run.event.entity.type.*;
import com.guide.run.user.entity.type.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetailEvent {
    private Long eventId;
    private EventType type;
    private String name;
    private EventRecruitStatus recruitStatus;
    private LocalDate recruitStartDate;//모집 시작일
    private LocalDate recruitEndDate;//모집 마감일
    private String organizerId;
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
    private Boolean isApply;
    private Boolean hasPartner;
    private List<EventDetailPartner> partner;

    private String details;
    private Boolean checkOrganizer;
    private EventStatus status;
    private EventCategory eventCategory;

    //지역
    private CityName cityName;

}
