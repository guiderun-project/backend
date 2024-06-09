package com.guide.run.user.profile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NaverProfile implements OAuthProfile{
    private String socialId;
    private String provider;
}
