package com.guide.run.event.entity.dto.response.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class GetAllForms {
    private List<FormWithPhone> vi;
    private List<FormWithPhone> guide;
}
