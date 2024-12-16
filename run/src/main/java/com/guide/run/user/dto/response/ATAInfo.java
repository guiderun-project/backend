package com.guide.run.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

@Getter
@AllArgsConstructor
@Builder
public class ATAInfo {
    @Value("${spring.coolsms.senderNumber}")
    private String senderNumber;
    @Value("${spring.coolsms.adminNumber}")
    private String adminNumber;

    @Setter
    private String userType;

    private String userName;

}
