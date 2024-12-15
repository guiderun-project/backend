package com.guide.run.event.entity.dto.request;

import com.guide.run.event.entity.type.EventCategory;
import com.guide.run.event.entity.type.EventType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class EventCreateRequest {
    private LocalDate recruitStartDate;//모집 시작일
    private LocalDate recruitEndDate;//모집 마감일
    private String name;//이벤트 제목
    private EventType eventType;//이벤트 분류
    private String date; //yyyy-mm-dd
    private String startTime;//hh:mm
    private String endTime;//hh:mm
    private int minNumV;//vi 모집 인원
    private int minNumG;//guide 모집 인원
    private String place;//이벤트 장소
    private String content;//이벤트 내용
    private EventCategory eventCategory; //이벤트 유형
}
