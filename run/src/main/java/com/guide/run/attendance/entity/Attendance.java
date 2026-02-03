package com.guide.run.attendance.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(AttendanceId.class)
@Table(indexes = {
    @Index(name = "idx_attendance_event_id", columnList = "eventId"),
    @Index(name = "idx_attendance_is_attend", columnList = "isAttend")
})
public class Attendance {
    @Id
    private String privateId;
    @Id
    private Long eventId;
    private boolean isAttend;
    private LocalDateTime date;

}
