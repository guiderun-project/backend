package com.guide.run.event.service;

import com.guide.run.event.entity.Event;
import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.event.entity.type.EventStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class EventTemporalStatusResolverTest {

    private final LocalDate today = LocalDate.of(2026, 6, 28);
    private final LocalDateTime now = LocalDateTime.of(2026, 6, 28, 12, 0);

    @Test
    @DisplayName("모집 시작 전이면 저장된 상태와 무관하게 모집 예정으로 계산한다")
    void resolveRecruitStatusReturnsUpcomingBeforeRecruitStartDate() {
        Event event = createEvent(
                EventRecruitStatus.RECRUIT_OPEN,
                today.plusDays(1),
                today.plusDays(3),
                now.plusDays(5),
                now.plusDays(5).plusHours(2)
        );

        EventRecruitStatus status = EventTemporalStatusResolver.resolveRecruitStatus(event, now, today);

        assertThat(status).isEqualTo(EventRecruitStatus.RECRUIT_UPCOMING);
    }

    @Test
    @DisplayName("모집 기간 중이고 이벤트 시작 전이면 모집중으로 계산한다")
    void resolveRecruitStatusReturnsOpenDuringRecruitPeriodBeforeEventStart() {
        Event event = createEvent(
                EventRecruitStatus.RECRUIT_UPCOMING,
                today.minusDays(1),
                today.plusDays(1),
                now.plusDays(2),
                now.plusDays(2).plusHours(2)
        );

        EventRecruitStatus status = EventTemporalStatusResolver.resolveRecruitStatus(event, now, today);

        assertThat(status).isEqualTo(EventRecruitStatus.RECRUIT_OPEN);
    }

    @Test
    @DisplayName("모집 종료일이 지나면 저장된 상태와 무관하게 모집 마감으로 계산한다")
    void resolveRecruitStatusReturnsCloseAfterRecruitEndDate() {
        Event event = createEvent(
                EventRecruitStatus.RECRUIT_OPEN,
                today.minusDays(5),
                today.minusDays(1),
                now.plusDays(2),
                now.plusDays(2).plusHours(2)
        );

        EventRecruitStatus status = EventTemporalStatusResolver.resolveRecruitStatus(event, now, today);

        assertThat(status).isEqualTo(EventRecruitStatus.RECRUIT_CLOSE);
    }

    @Test
    @DisplayName("이벤트가 시작됐으면 모집 종료일이 남아 있어도 모집 마감으로 계산한다")
    void resolveRecruitStatusReturnsCloseAfterEventStart() {
        Event event = createEvent(
                EventRecruitStatus.RECRUIT_OPEN,
                today.minusDays(1),
                today.plusDays(1),
                now.minusMinutes(1),
                now.plusHours(2)
        );

        EventRecruitStatus status = EventTemporalStatusResolver.resolveRecruitStatus(event, now, today);

        assertThat(status).isEqualTo(EventRecruitStatus.RECRUIT_CLOSE);
    }

    @Test
    @DisplayName("저장된 모집 상태가 조기 마감이면 시간이 남아 있어도 모집 마감으로 유지한다")
    void resolveRecruitStatusKeepsManualClose() {
        Event event = createEvent(
                EventRecruitStatus.RECRUIT_CLOSE,
                today.minusDays(1),
                today.plusDays(1),
                now.plusDays(2),
                now.plusDays(2).plusHours(2)
        );

        EventRecruitStatus status = EventTemporalStatusResolver.resolveRecruitStatus(event, now, today);

        assertThat(status).isEqualTo(EventRecruitStatus.RECRUIT_CLOSE);
    }

    @Test
    @DisplayName("이벤트 시간 기준으로 예정, 진행중, 종료 상태를 계산한다")
    void resolveEventStatusByEventTime() {
        assertThat(EventTemporalStatusResolver.resolveEventStatus(createEvent(
                EventRecruitStatus.RECRUIT_OPEN,
                today,
                today,
                now.plusMinutes(1),
                now.plusHours(1)
        ), now)).isEqualTo(EventStatus.EVENT_UPCOMING);

        assertThat(EventTemporalStatusResolver.resolveEventStatus(createEvent(
                EventRecruitStatus.RECRUIT_OPEN,
                today,
                today,
                now.minusMinutes(1),
                now.plusHours(1)
        ), now)).isEqualTo(EventStatus.EVENT_OPEN);

        assertThat(EventTemporalStatusResolver.resolveEventStatus(createEvent(
                EventRecruitStatus.RECRUIT_OPEN,
                today,
                today,
                now.minusHours(2),
                now.minusMinutes(1)
        ), now)).isEqualTo(EventStatus.EVENT_END);
    }

    @Test
    @DisplayName("이벤트 종료 시각 정각이면 종료 상태로 계산한다")
    void resolveEventStatusReturnsEndAtExactEndTime() {
        Event event = createEvent(
                EventRecruitStatus.RECRUIT_OPEN,
                today,
                today,
                now.minusHours(2),
                now
        );

        EventStatus status = EventTemporalStatusResolver.resolveEventStatus(event, now);

        assertThat(status).isEqualTo(EventStatus.EVENT_END);
    }

    @Test
    @DisplayName("모집 상태 raw 필드 계산은 같은 현재 시각에서 날짜를 파생한다")
    void resolveRecruitStatusWithRawFieldsDerivesTodayFromSameNow() {
        LocalDate serviceToday = EventTemporalStatusResolver.today();
        LocalDateTime serviceNow = EventTemporalStatusResolver.now();

        EventRecruitStatus status = EventTemporalStatusResolver.resolveRecruitStatus(
                EventRecruitStatus.RECRUIT_UPCOMING,
                serviceToday.minusDays(1),
                serviceToday.plusDays(1),
                serviceNow.plusDays(2)
        );

        assertThat(status).isEqualTo(EventRecruitStatus.RECRUIT_OPEN);
    }

    private Event createEvent(EventRecruitStatus recruitStatus,
                              LocalDate recruitStartDate,
                              LocalDate recruitEndDate,
                              LocalDateTime startTime,
                              LocalDateTime endTime) {
        return Event.builder()
                .recruitStatus(recruitStatus)
                .recruitStartDate(recruitStartDate)
                .recruitEndDate(recruitEndDate)
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }
}
