package com.guide.run.user.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public enum Role {
    ADMIN("ROLE_ADMIN"),
    WAIT("ROLE_WAIT"),
    COACH("ROLE_COACH"),
    VI("ROLE_VI"),
    GUIDE("ROLE_GUIDE");
    private final String value;
}
