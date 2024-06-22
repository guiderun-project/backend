package com.guide.run.event.entity.dto.response.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class GetAllForms {
    private List<Form> vi;
    private List<Form> guide;
}
