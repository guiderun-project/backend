package com.guide.run.event.entity;

import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.event.entity.type.EvenvtType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //이벤트 id
    private String organizer; //주최자 id
    private LocalDate recruitStartDate;//모집 시작일
    private LocalDate recruitEndDate;//모집 마감일
    private String name; //이벤트 제목
    private EventRecruitStatus recruitStatus; // event 모집 상태 대기,승인,모집마감
    private boolean isApprove; // 이벤트 승인 여부
    private EvenvtType type;//이벤트 분류
    private LocalDateTime startTime;//이벤트 시작일+ 시작시간
    private LocalDateTime endTime;//이벤트 종료일 + 이벤트 종료 시간
    private int maxNumV;//vi 모집 인원
    private int maxNumG;//guide 모집 인원
    private String place;//이벤트 장소
    private String content;//이벤트 내용
}
