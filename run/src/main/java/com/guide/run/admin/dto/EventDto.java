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
    private String startTime;
    private String organizer;
    private String pace;
    private String recruitStatus;
    private boolean approval;
    private int maxApply;
    private int maxNumV;
    private int maxNumG;
    private String update_date;
    private String update_time;

    public EventDto(Long eventId,
                    String title,
                    String smallDate,
                    String startTime,
                    String organizer,
                    String pace,
                    EventRecruitStatus recruitStatus,
                    boolean approval,
                    int maxApply,
                    int maxNumV,
                    int maxNumG,
                    String update_date,
                    String update_time) {
        this.eventId = eventId;
        this.title = title;
        this.smallDate = smallDate;
        this.startTime = startTime;
        this.organizer = organizer;
        this.pace = pace;
        this.recruitStatus = "RECRUIT_" + recruitStatus.getValue();
        this.approval = approval;
        this.maxApply = maxApply;
        this.maxNumV = maxNumV;
        this.maxNumG = maxNumG;
        this.update_date = update_date;
        this.update_time = update_time;
    }
}
