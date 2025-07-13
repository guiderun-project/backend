package com.guide.run.event.entity.dto.response.form;

import com.guide.run.user.entity.type.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class FormWithPhone {
    private String userId;
    private UserType type;
    private String applyRecord;
    private String name;
    private String recordDegree;
    private String phone;

    public FormWithPhone(String userId, UserType type, String applyRecord, String name, String recordDegree) {
        this.userId = userId;
        this.type = type;
        this.applyRecord = applyRecord;
        this.name = name;
        this.recordDegree = recordDegree;
        this.phone = null;
    }
}
