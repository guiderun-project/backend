package com.guide.run.admin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Guide1365Response {
    private String name;
    private String id1365;
    private String phone;
    private String birth;
}
