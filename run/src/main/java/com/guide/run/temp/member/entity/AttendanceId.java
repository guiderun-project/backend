package com.guide.run.temp.member.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceId implements Serializable {
    private String privateId;
    private Long eventId;
}