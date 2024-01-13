package com.guide.run.user.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public enum Role {
    VADMIN("ROLE_VADMIN"),
    GADMIN("ROLE_GADMIN"),
    VWAIT("ROLE_VWAIT"),
    GWAIT("ROLE_GWAIT"),
    VCOACH("ROLE_VCOACH"),
    GCOACH("ROLE_GCOACH"),
    VI("ROLE_VI"),
    GUIDE("ROLE_GUIDE");
    private final String value;
}
