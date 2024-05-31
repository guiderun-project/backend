package com.guide.run.admin.dto;

import com.guide.run.event.entity.type.EventRecruitStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class EventDto {
    private Long eventId;
    private String title;
    private String smallDate;
    private String date;
    private String organizer;
    private String pace;
    private EventRecruitStatus recruitStatus;
    private boolean approval;
    private int participation;
    private int viParticipation;
    private int guideParticipation;
    private String update_date;
    private String update_time;
}
