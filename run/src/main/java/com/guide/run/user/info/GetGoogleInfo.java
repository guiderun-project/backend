package com.guide.run.user.info;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetGoogleInfo {
    //google은 sub로 id값을 넘겨줌
    private String sub;
}
