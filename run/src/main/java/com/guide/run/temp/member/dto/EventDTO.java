package com.guide.run.temp.member.dto;

import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.event.entity.type.EventType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventDTO {
    private Long id; //이벤트 id
    private String organizer = "458"; //주최자 id
    private String name; //이벤트 제목
    private EventRecruitStatus recruitStatus = EventRecruitStatus.END; // event 모집 상태 대기,모집중,모집마감
    private boolean isApprove = true; // 이벤트 승인 여부
    private EventType type;//이벤트 분류
    private LocalDateTime startTime;//이벤트 시작일+ 시작시간
    private LocalDateTime endTime;//이벤트 종료일 + 이벤트 종료 시간
    private String place;//이벤트 장소
    private String content;//이벤트 내용
    private int viCnt; //실제 모집된 vi 수
    private int guideCnt; //실제 모집된 guide 수
}
