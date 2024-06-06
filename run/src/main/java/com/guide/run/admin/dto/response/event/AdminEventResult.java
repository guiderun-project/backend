package com.guide.run.admin.dto.response.event;

import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.event.entity.type.EventStatus;
import com.guide.run.event.entity.type.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class AdminEventResult {

    private String name;
    private EventType type;
    private boolean approval;
    private String date;
    private String organizer;
    private String pace;
    private EventStatus status;
    private EventRecruitStatus recruitStatus;
    private int total;
    private int viCnt;
    private int guideCnt;
    private int absent;
    private int viAbsent;
    private int guideAbsent;

}
