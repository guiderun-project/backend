package com.guide.run.event.entity.dto.response;

import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.event.entity.type.EventStatus;
import com.guide.run.event.entity.type.EventType;
import com.guide.run.user.entity.type.UserType;
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
        private String organizer ; //주최자 이름
        private UserType organizerType; //주최자 장애유무
        private String organizerRecord ; //주최자 러닝등급
        private String name;//이벤트 제목
        private EventRecruitStatus recruitStatus; //이벤트 모집 상태
        private LocalDate date; //이벤트 시작일
        private String startTime;//시작시간
        private String endTime;//이벤트 종료 시간

        private int recruitVi; //모집 예정인 vi 수
        private int recruitGuide;//모집 예정인 guide 수
        private int viCnt; //실제 모집된 vi 수
        private int guideCnt; //실제 모집된 guide 수
        private String place;//이벤트 장소
        private String content;//이벤트 내용
        private LocalDate updatedAt; //수정일

        private Boolean isApply; //신청 여부
        private Boolean hasPartner; //파트너 존재 여부
        private String partnerName; //파트너 이름
        private String partnerRecord; //파트너 러닝 등급
        private UserType partnerType; //파트너 타입(가이드인지 vi인지)

        private EventStatus status; //이벤트 status

        public void setPartner(Boolean hasPartner, String partnerName, String partnerRecord, UserType partnerType){
                this.hasPartner = hasPartner;
                this.partnerName = partnerName;
                this.partnerRecord = partnerRecord;
                this.partnerType = partnerType;
        }

}
