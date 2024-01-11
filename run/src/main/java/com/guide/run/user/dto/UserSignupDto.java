package com.guide.run.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSignupDto {
    private String name;
    private String gender;
    private String phoneNumber;
    private int age;
    private String detailRecord;
    private String recordDegree; //개인 기록
    //private List<Event> eventLists;
    //private List<User> partnerLists;
    private String snsId;
    private boolean runningExp;
    private String guideName;
}
