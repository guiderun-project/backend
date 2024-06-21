package com.guide.run.user.entity.type;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public enum Role {
    ROLE_ADMIN("ROLE_ADMIN"),
    ROLE_REJECT("ROLE_REJECT"),
    ROLE_WAIT("ROLE_WAIT"),
    ROLE_COACH("ROLE_COACH"),
    ROLE_USER("ROLE_USER"),
    ROLE_NEW("ROLE_NEW"),
    ROLE_DELETE("ROLE_DELETE");
    private final String value;
}