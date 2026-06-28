package com.guide.run.event.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guide.run.event.entity.dto.response.comments.GetComment;
import com.guide.run.event.entity.dto.response.get.AllEvent;
import com.guide.run.event.entity.dto.response.get.AllEventResponse;
import com.guide.run.event.entity.dto.response.get.MyEventDday;
import com.guide.run.event.entity.dto.response.get.UpcomingEventResponse;
import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.event.entity.type.EventType;
import com.guide.run.user.entity.type.UserType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EventResponseContractTest {

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Test
    @DisplayName("이벤트 목록 응답은 Notion 계약의 필드명과 1-based pagination을 사용한다")
    void eventListResponseUsesNotionContract() throws Exception {
        AllEvent item = new AllEvent(
                101L,
                EventType.TRAINING,
                "한강 러닝 모임",
                LocalDateTime.of(2026, 4, 8, 19, 0),
                EventRecruitStatus.RECRUIT_END
        );
        AllEventResponse response = AllEventResponse.builder()
                .items(List.of(item))
                .pagination(AllEventResponse.Pagination.builder()
                        .page(1)
                        .size(10)
                        .totalCount(27)
                        .totalPages(3)
                        .hasNext(true)
                        .build())
                .build();

        JsonNode json = objectMapper.readTree(objectMapper.writeValueAsString(response));

        assertThat(json.at("/items/0/id").asLong()).isEqualTo(101L);
        assertThat(json.at("/items/0/type").asText()).isEqualTo("TRAINING");
        assertThat(json.at("/items/0/dateText").asText()).isEqualTo("2026. 04. 08 수");
        assertThat(json.at("/items/0/recruitStatus").asText()).isEqualTo("RECRUIT_CLOSE");
        assertThat(json.at("/items/0/eventId").isMissingNode()).isTrue();
        assertThat(json.at("/items/0/eventType").isMissingNode()).isTrue();
        assertThat(json.at("/items/0/startDate").isMissingNode()).isTrue();
        assertThat(json.at("/pagination/page").asInt()).isEqualTo(1);
        assertThat(json.at("/pagination/size").asInt()).isEqualTo(10);
        assertThat(json.at("/pagination/totalCount").asLong()).isEqualTo(27L);
        assertThat(json.at("/pagination/totalPages").asInt()).isEqualTo(3);
        assertThat(json.at("/pagination/hasNext").asBoolean()).isTrue();
    }

    @Test
    @DisplayName("이벤트 목록 항목은 projection의 시간 필드로 현재 모집 상태를 계산한다")
    void allEventComputesRecruitStatusFromTemporalFields() {
        LocalDate today = EventTemporalStatusResolver.today();
        LocalDateTime now = EventTemporalStatusResolver.now();

        AllEvent item = new AllEvent(
                101L,
                EventType.TRAINING,
                "한강 러닝 모임",
                now.plusDays(3),
                now.plusDays(3).plusHours(2),
                today.minusDays(5),
                today.minusDays(1),
                EventRecruitStatus.RECRUIT_OPEN
        );

        assertThat(item.getRecruitStatus()).isEqualTo(EventRecruitStatus.RECRUIT_CLOSE);
    }

    @Test
    @DisplayName("나의 이벤트 D-day 항목은 날짜 기준으로 남은 일수를 계산한다")
    void myEventDdayUsesDateBasedDayCount() {
        LocalDateTime eventDate = EventTemporalStatusResolver.now().plusDays(2);

        MyEventDday item = new MyEventDday("한강 러닝 모임", eventDate);

        assertThat(item.getDDay()).isEqualTo(2L);
    }

    @Test
    @DisplayName("다가오는 모임 비회원 응답은 viewerType=GUEST와 공개 모임 필드만 사용한다")
    void upcomingGuestResponseUsesNotionContract() throws Exception {
        UpcomingEventResponse response = UpcomingEventResponse.guest(List.of(
                UpcomingEventResponse.GuestItem.builder()
                        .id(101L)
                        .name("한강 러닝 모임")
                        .dDay(3)
                        .date(LocalDate.of(2026, 5, 14))
                        .build()
        ));

        JsonNode json = objectMapper.readTree(objectMapper.writeValueAsString(response));

        assertThat(json.at("/viewerType").asText()).isEqualTo("GUEST");
        assertThat(json.at("/items/0/id").asLong()).isEqualTo(101L);
        assertThat(json.at("/items/0/name").asText()).isEqualTo("한강 러닝 모임");
        assertThat(json.at("/items/0/dDay").asInt()).isEqualTo(3);
        assertThat(json.at("/items/0/date").asText()).isEqualTo("2026-05-14");
        assertThat(json.at("/items/0/place").isMissingNode()).isTrue();
        assertThat(json.at("/items/0/myPartner").isMissingNode()).isTrue();
    }

    @Test
    @DisplayName("다가오는 모임 회원 응답은 viewerType=MEMBER와 회원 전용 필드를 사용한다")
    void upcomingMemberResponseUsesNotionContract() throws Exception {
        UpcomingEventResponse response = UpcomingEventResponse.member(List.of(
                UpcomingEventResponse.MemberItem.builder()
                        .id(101L)
                        .name("한강 러닝 모임")
                        .dDay(3)
                        .place("여의나루역 2번 출구")
                        .scheduleText("2026년 5월 14일 (목) 오전 10시 ~ 오후 12시")
                        .myPartner(List.of(
                                UpcomingEventResponse.PartnerItem.builder()
                                        .type(UserType.VI)
                                        .name("홍길동")
                                        .build()
                        ))
                        .build()
        ));

        JsonNode json = objectMapper.readTree(objectMapper.writeValueAsString(response));

        assertThat(json.at("/viewerType").asText()).isEqualTo("MEMBER");
        assertThat(json.at("/items/0/id").asLong()).isEqualTo(101L);
        assertThat(json.at("/items/0/dDay").asInt()).isEqualTo(3);
        assertThat(json.at("/items/0/place").asText()).isEqualTo("여의나루역 2번 출구");
        assertThat(json.at("/items/0/scheduleText").asText()).isEqualTo("2026년 5월 14일 (목) 오전 10시 ~ 오후 12시");
        assertThat(json.at("/items/0/myPartner/0/type").asText()).isEqualTo("VI");
        assertThat(json.at("/items/0/myPartner/0/name").asText()).isEqualTo("홍길동");
        assertThat(json.at("/items/0/date").isMissingNode()).isTrue();
    }

    @Test
    @DisplayName("댓글 목록 항목의 createdAt은 초 단위까지 포함한 ISO datetime 문자열을 사용한다")
    void commentCreatedAtIncludesTime() throws Exception {
        GetComment comment = new GetComment(
                301L,
                "홍길동",
                "guide_102",
                UserType.GUIDE,
                "이번 주 토요일에도 참여할게요.",
                LocalDateTime.of(2026, 6, 24, 13, 45, 30)
        );

        JsonNode json = objectMapper.readTree(objectMapper.writeValueAsString(comment));

        assertThat(json.at("/createdAt").asText()).isEqualTo("2026-06-24T13:45:30");
    }
}
