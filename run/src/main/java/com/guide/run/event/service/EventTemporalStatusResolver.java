package com.guide.run.event.service;

import com.guide.run.event.entity.Event;
import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.event.entity.type.EventStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

public final class EventTemporalStatusResolver {
    private static final ZoneId SERVICE_ZONE = ZoneId.of("Asia/Seoul");

    private EventTemporalStatusResolver() {
    }

    public static LocalDateTime now() {
        return LocalDateTime.now(SERVICE_ZONE);
    }

    public static LocalDate today() {
        return LocalDate.now(SERVICE_ZONE);
    }

    public static EventRecruitStatus resolveRecruitStatus(Event event) {
        LocalDateTime now = now();
        return resolveRecruitStatus(event, now, now.toLocalDate());
    }

    public static EventRecruitStatus resolveRecruitStatus(Event event, LocalDateTime now, LocalDate today) {
        return resolveRecruitStatus(
                event.getRecruitStatus(),
                event.getRecruitStartDate(),
                event.getRecruitEndDate(),
                event.getStartTime(),
                now,
                today
        );
    }

    public static EventRecruitStatus resolveRecruitStatus(EventRecruitStatus storedRecruitStatus,
                                                          LocalDate recruitStartDate,
                                                          LocalDate recruitEndDate,
                                                          LocalDateTime startTime) {
        LocalDateTime now = now();
        return resolveRecruitStatus(
                storedRecruitStatus,
                recruitStartDate,
                recruitEndDate,
                startTime,
                now,
                now.toLocalDate()
        );
    }

    public static EventRecruitStatus resolveRecruitStatus(EventRecruitStatus storedRecruitStatus,
                                                          LocalDate recruitStartDate,
                                                          LocalDate recruitEndDate,
                                                          LocalDateTime startTime,
                                                          LocalDateTime now,
                                                          LocalDate today) {
        if (storedRecruitStatus == EventRecruitStatus.RECRUIT_CLOSE) {
            return EventRecruitStatus.RECRUIT_CLOSE;
        }
        if (startTime != null && !now.isBefore(startTime)) {
            return EventRecruitStatus.RECRUIT_CLOSE;
        }
        if (recruitStartDate != null && today.isBefore(recruitStartDate)) {
            return EventRecruitStatus.RECRUIT_UPCOMING;
        }
        if (recruitEndDate != null && today.isAfter(recruitEndDate)) {
            return EventRecruitStatus.RECRUIT_CLOSE;
        }
        return EventRecruitStatus.RECRUIT_OPEN;
    }

    public static EventStatus resolveEventStatus(Event event) {
        return resolveEventStatus(event, now());
    }

    public static EventStatus resolveEventStatus(Event event, LocalDateTime now) {
        if (event.getStartTime() != null && now.isBefore(event.getStartTime())) {
            return EventStatus.EVENT_UPCOMING;
        }
        if (event.getEndTime() != null && !now.isBefore(event.getEndTime())) {
            return EventStatus.EVENT_END;
        }
        return EventStatus.EVENT_OPEN;
    }
}
