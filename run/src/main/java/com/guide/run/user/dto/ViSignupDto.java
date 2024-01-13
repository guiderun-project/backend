package com.guide.run.user.dto;

import com.guide.run.user.entity.type.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ViSignupDto {
    //private String accountId;
    //private String password;
    private String name;
    private String gender;
    private String phoneNumber;
    private boolean openNumber;
    private int age;
    private String detailRecord;
    private String recordDegree; //개인 기록
    private String snsId;
    private boolean openSNS;
    private boolean runningExp;
    private String guideName;
}
