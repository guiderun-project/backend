package com.guide.run.temp.member.dto;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttDTO {
    private String privateId;
    private Long eventId;
    private boolean isAttend;
    private LocalDateTime date;
}
