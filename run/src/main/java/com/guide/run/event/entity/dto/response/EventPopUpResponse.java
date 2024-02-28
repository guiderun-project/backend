package com.guide.run.event.entity.dto.response;

import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.event.entity.type.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventPopUpResponse {

        private Long eventId;
        private EventType type;//이벤트 분류
        private String name;//이벤트 제목
        private EventRecruitStatus recruitStatus; //이벤트 모집 상태
        private LocalDate date; //이벤트 시작일
        private String startTime;//시작시간
        private String endTime;//이벤트 종료 시간

        private int viCnt; //실제 모집된 vi 수
        private int guideCnt; //실제 모집된 guide 수
        private String place;//이벤트 장소
        private String content;//이벤트 내용
        private LocalDate updatedAt; //수정일

}
