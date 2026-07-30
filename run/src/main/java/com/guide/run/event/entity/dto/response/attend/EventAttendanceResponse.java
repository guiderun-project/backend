package com.guide.run.event.entity.dto.response.attend;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class EventAttendanceResponse {
    private Summary summary;
    private List<AttendanceParticipant> waiting;
    private List<AttendanceParticipant> attended;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Summary {
        private long waitingCount;
        private long attendedCount;
    }
}
