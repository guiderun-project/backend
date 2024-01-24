package com.guide.run.event.entity.dto.request;

import com.guide.run.event.entity.type.EvenvtType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class EventCreateRequest {
    private LocalDate recruitStartDate;//모집 시작일
    private LocalDate recruitEndDate;//모집 마감일
    private String name;//이벤트 제목
    private boolean isRecruited; // 모집 상태 true면 모집중 false면 모집 마감
    private EvenvtType type;//이벤트 분류
    private LocalDateTime startTime;//이벤트 시작일+ 시작시간
    private LocalDateTime endTime;//이벤트 종료일 + 이벤트 종료 시간
    private int maxNumV;//vi 모집 인원
    private int maxNumG;//guide 모집 인원
    private String place;//이벤트 장소
    private String content;//이벤트 내용
}
