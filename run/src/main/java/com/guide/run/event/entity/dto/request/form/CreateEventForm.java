package com.guide.run.event.entity.dto.request.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class CreateEventForm {
    private String group;
    private String partner;
    private String detail;
}
