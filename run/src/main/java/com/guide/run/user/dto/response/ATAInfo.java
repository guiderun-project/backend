package com.guide.run.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;

@Getter
@AllArgsConstructor
@Builder
public class ATAInfo {

    private ArrayList<String> toList;

    @Setter
    private String userType;

    private String userName;

}
