package com.guide.run.user.entity.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserType {
    VI("VI"), GUIDE("GUIDE");
    private final String value;
}
