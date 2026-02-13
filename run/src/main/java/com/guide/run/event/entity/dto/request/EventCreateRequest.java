package com.guide.run.event.entity.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import com.guide.run.event.entity.type.CityName;
import com.guide.run.event.entity.type.EventCategory;
import com.guide.run.event.entity.type.EventType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@Schema(description = "이벤트 생성 요청", example = "{\"name\":\"상계천천히달리기\",\"eventType\":\"RUN\",\"date\":\"2026-02-20\",\"startTime\":\"09:00\",\"endTime\":\"11:00\",\"cityName\":\"SEOUL\"}")
public class EventCreateRequest {
    @Schema(description = "모집 시작일", example = "2026-02-13")
    private LocalDate recruitStartDate;//모집 시작일
    @Schema(description = "모집 마감일", example = "2026-02-18")
    private LocalDate recruitEndDate;//모집 마감일
    @Schema(description = "이벤트 제목", example = "상계천천히달리기")
    private String name;//이벤트 제목
    @Schema(description = "이벤트 유형", example = "RUN")
    private EventType eventType;//이벤트 분류
    @Schema(description = "이벤트 일자", example = "2026-02-20")
    private String date; //yyyy-mm-dd
    @Schema(description = "시작 시간", example = "09:00")
    private String startTime;//hh:mm
    @Schema(description = "종료 시간", example = "11:00")
    private String endTime;//hh:mm
    @Schema(description = "VI 모집 인원", example = "4")
    private int minNumV;//vi 모집 인원
    @Schema(description = "Guide 모집 인원", example = "2")
    private int minNumG;//guide 모집 인원
    @Schema(description = "이벤트 장소", example = "서울 상계천천히공원")
    private String place;//이벤트 장소
    @Schema(description = "이벤트 상세 내용", example = "초급자 대상 오전 러닝 행사")
    private String content;//이벤트 내용
    @Schema(description = "이벤트 카테고리", example = "TEAM")
    private EventCategory eventCategory; //이벤트 유형
    @Schema(description = "도시명", example = "SEOUL")
    private CityName cityName; //이벤트 지역
}
