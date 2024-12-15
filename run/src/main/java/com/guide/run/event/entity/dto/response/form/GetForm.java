package com.guide.run.event.entity.dto.response.form;


import com.guide.run.event.entity.type.EventCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class GetForm {
    private String type;
    private String name;
    private String pace;
    private String group;
    private String partner;
    private String detail;
    private EventCategory eventCategory;
}
