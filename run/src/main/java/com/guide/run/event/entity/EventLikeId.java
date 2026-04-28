package com.guide.run.event.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class EventLikeId implements Serializable {
    private Long eventId;
    private String privateId;
}
