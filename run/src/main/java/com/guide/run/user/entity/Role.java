package com.guide.run.user.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public enum Role {
    ADMIN("ROLE_ADMIN"),
    User("ROLE_USER"),
    COACH("ROLE_COACH"),
    VI("ROLE_VI"),
    GUIDE("ROLE_GUIDE");
    private String value;
}
