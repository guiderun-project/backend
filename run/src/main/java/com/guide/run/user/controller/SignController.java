package com.guide.run.user.controller;

import com.guide.run.gloabl.cookie.service.CookieService;
import com.guide.run.gloabl.jwt.JwtProvider;
import com.guide.run.user.response.LoginResponse;
import com.guide.run.user.profile.OAuthProfile;
import com.guide.run.user.service.ProviderService;
import com.guide.run.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.naming.CommunicationException;



@RestController
@RequiredArgsConstructor
public class SignController {
    private final ProviderService providerService;
    private final JwtProvider jwtProvider;
    private final CookieService cookieService;
    private final UserService userService;

    @PostMapping("/api/oauth/login/kakao")
    public LoginResponse kakaoLogin(String code, HttpServletResponse response) throws CommunicationException {
        String accessToken = providerService.getAccessToken(code, "kakao").getAccess_token();
        OAuthProfile oAuthProfile = providerService.getProfile(accessToken,"kakao");
        String socialId = oAuthProfile.getSocialId();

        cookieService.createCookie("refreshToken",response);

        return LoginResponse.builder()
                .accessToken(jwtProvider.createAccessToken(socialId))
                .userStatus(userService.getUserStatus(socialId))
                .build();
    }



    @PostMapping("/api/oauth/token/google")
    public LoginResponse googleSignup(String code,HttpServletResponse response) throws CommunicationException {
        String accessToken = providerService.getAccessToken(code, "google").getAccess_token();
        OAuthProfile oAuthProfile = providerService.getProfile(accessToken,"google");
        String socialId = oAuthProfile.getSocialId();

        return LoginResponse.builder()
                .accessToken(jwtProvider.createAccessToken(socialId))
                .build();
    }

}
