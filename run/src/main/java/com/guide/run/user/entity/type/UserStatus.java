package com.guide.run.user.entity.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserStatus {
    EXIST("exist"),
    WAIT("wait"),
    NEW("new");
    private final String value;
}
