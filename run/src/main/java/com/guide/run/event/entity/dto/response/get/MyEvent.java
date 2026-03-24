package com.guide.run.event.entity.dto.response.get;

import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.event.entity.type.EventType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "나의 이벤트 목록 항목")
public class MyEvent {
    @Schema(description = "이벤트 ID", example = "1023")
    private Long eventId;
    @Schema(description = "이벤트 유형", example = "TRAINING")
    private EventType eventType;
    @Schema(description = "이벤트 이름", example = "상계천 천천히 달리기")
    private String name;
    @Schema(description = "D-day", example = "7")
    private int dDay;
    private LocalDate endDate;
    private EventRecruitStatus recruitStatus;

    public MyEvent(Long eventId, EventType eventType, String name, EventRecruitStatus recruitStatus, LocalDateTime endDate) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.name = name;
        this.recruitStatus = recruitStatus;
        this.endDate = endDate.toLocalDate();
    }

    public void setdDay(int dDay) {
        this.dDay = dDay;
    }
}
