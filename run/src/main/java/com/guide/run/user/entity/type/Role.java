package com.guide.run.user.entity.type;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public enum Role {
    ROLE_ADMIN("ADMIN"),
    ROLE_REJECT("REJECT"),
    ROLE_WAIT("WAIT"),
    ROLE_COACH("COACH"),
    ROLE_USER("USER"),
    ROLE_NEW("NEW");
    private final String value;
}