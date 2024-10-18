package com.guide.run.event.entity.dto.response.form;

import com.guide.run.user.entity.type.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class Form {
    private String userId;
    private UserType type;
    private String applyRecord;
    private String name;
    private String recordDegree;
}
