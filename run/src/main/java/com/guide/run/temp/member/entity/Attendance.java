package com.guide.run.temp.member.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
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
public class Attendance {
    @Id
    private String privateId;
    @Id
    private Long eventId;
    private boolean isAttend;
    private LocalDateTime date;

}
