package com.guide.run.user.profile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoogleProfile implements OAuthProfile{
    private String socialId;
    private String provider;
}
