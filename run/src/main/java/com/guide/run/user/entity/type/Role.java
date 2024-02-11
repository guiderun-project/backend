package com.guide.run.user.entity.type;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public enum Role {
    ADMIN("ADMIN"),
    WAIT("WAIT"),
    COACH("COACH"),
    USER("USER"),
    NEW("NEW");
    private final String value;
}