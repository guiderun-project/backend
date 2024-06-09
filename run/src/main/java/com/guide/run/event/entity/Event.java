package com.guide.run.event.entity;

import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.event.entity.type.EventType;
import com.guide.run.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event extends BaseEntity {
    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY) //todo : 이 부분 기존 이벤트 추가로 적용하려면 db 수정 필요
    private Long id; //이벤트 id
    private String organizer; //주최자 id
    private LocalDate recruitStartDate;//모집 시작일
    private LocalDate recruitEndDate;//모집 마감일
    private String name; //이벤트 제목
    @Enumerated(EnumType.STRING)
    private EventRecruitStatus recruitStatus; // event 모집 상태 대기,모집중,모집마감
    private boolean isApprove; // 이벤트 승인 여부
    @Enumerated(EnumType.STRING)
    private EventType type;//이벤트 분류
    private LocalDateTime startTime;//이벤트 시작일+ 시작시간
    private LocalDateTime endTime;//이벤트 종료일 + 이벤트 종료 시간
    private int maxNumV;//vi 모집 인원
    private int maxNumG;//guide 모집 인원
    private String place;//이벤트 장소
    private String content;//이벤트 내용

    //이 부분 추가됐습니다~
    private int viCnt; //실제 모집된 vi 수
    private int guideCnt; //실제 모집된 guide 수
    @Enumerated(EnumType.STRING)
    private EventStatus status;

    public void closeEvent() {
    }
}
