package com.guide.run.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewPasswordDto {
    private String token;
    private String newPassword;
}
