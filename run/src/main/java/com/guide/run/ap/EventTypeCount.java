package com.guide.run.ap;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class EventTypeCount {
    private int Training;
    private int Competition;
}
