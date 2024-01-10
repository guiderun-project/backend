package com.guide.run.user.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserStatus {
    USER("user"),
    WAIT("wait"),
    NEW("new");
    private String value;
}
