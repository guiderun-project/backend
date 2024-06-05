package com.guide.run.event.entity.dto.response.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class CreatedForm {
    private Long requestId;
}
