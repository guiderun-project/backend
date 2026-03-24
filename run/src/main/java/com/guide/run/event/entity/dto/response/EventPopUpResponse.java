package com.guide.run.event.entity.dto.response;

import com.guide.run.event.entity.type.*;
import com.guide.run.user.entity.type.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "이벤트 팝업/모달 상세 응답")
public class EventPopUpResponse {
        @Schema(description = "이벤트 ID", example = "1023")
        private Long eventId;
        @Schema(description = "이벤트 유형", example = "TRAINING")
        private EventType type;//이벤트 분류

        private String organizerId; //주최자 아이디
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

        @Schema(description = "현재 로그인 사용자의 신청 여부", example = "true")
        private Boolean isApply; //신청 여부
        @Schema(description = "파트너 정보 존재 여부", example = "false")
        private Boolean hasPartner; //파트너 존재 여부

        private List<EventPopUpPartner> partner;

        private EventStatus status; //이벤트 status
        private EventCategory eventCategory;

        //지역
        private CityName cityName;


        public void setPartner(Boolean isApply, Boolean hasPartner, List<EventPopUpPartner> partner){
                this.isApply = isApply;
                this.hasPartner = hasPartner;
                this.partner = partner;
        }

}
