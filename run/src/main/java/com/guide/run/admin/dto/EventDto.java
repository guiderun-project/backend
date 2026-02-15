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
    private String name;
    private String smallDate;
    private String startTime;
    private String organizer;
    private String pace;
    private EventRecruitStatus recruitStatus;
    private boolean approval;
    private int minApply;
    private int minNumV;
    private int minNumG;
    private String update_date;
    private String update_time;

    public EventDto(Long eventId,
                    String name,
                    String smallDate,
                    String startTime,
                    String organizer,
                    String pace,
                    EventRecruitStatus recruitStatus,
                    boolean approval,
                    int minNumV,
                    int minNumG,
                    String update_date,
                    String update_time) {
        this.eventId = eventId;
        this.name = name;
        this.smallDate = smallDate;
        this.startTime = startTime;
        this.organizer = organizer;
        this.pace = pace;
        this.recruitStatus = recruitStatus;
        this.approval = approval;
        this.minApply = minNumG+minNumV;
        this.minNumV = minNumV;
        this.minNumG = minNumG;
        this.update_date = update_date;
        this.update_time = update_time;
    }
}
