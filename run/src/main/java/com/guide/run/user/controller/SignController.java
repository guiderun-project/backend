package com.guide.run.user.controller;

import com.guide.run.gloabl.jwt.JwtProvider;
import com.guide.run.user.response.LoginResponse;
import com.guide.run.user.profile.OAuthProfile;
import com.guide.run.user.service.ProviderService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.CommunicationException;

@RestController
@RequiredArgsConstructor
public class SignController {
    private final ProviderService providerService;
    private final JwtProvider jwtProvider;

    @PostMapping("/oauth/token/kakao")
    public LoginResponse kakaoSignup(String code, HttpServletResponse response) throws CommunicationException {
        String accessToken = providerService.getAccessToken(code, "kakao").getAccess_token();
        OAuthProfile oAuthProfile = providerService.getProfile(accessToken,"kakao");
        String socialId = oAuthProfile.getSocialId();

        Cookie cookie = new Cookie("refresh",jwtProvider.createRefreshToken());
        cookie.setHttpOnly(true);
        response.addCookie(cookie);

        return LoginResponse.builder()
                .accessToken(jwtProvider.createAccessToken(socialId))
                .build();
    }

    @PostMapping("/oauth/token/google")
    public LoginResponse googleSignup(String code,HttpServletResponse response) throws CommunicationException {
        String accessToken = providerService.getAccessToken(code, "google").getAccess_token();
        OAuthProfile oAuthProfile = providerService.getProfile(accessToken,"google");
        String socialId = oAuthProfile.getSocialId();

        Cookie cookie = new Cookie("refresh",jwtProvider.createRefreshToken());
        cookie.setHttpOnly(true);
        response.addCookie(cookie);

        return LoginResponse.builder()
                .accessToken(jwtProvider.createAccessToken(socialId))
                .build();
    }

    @GetMapping("/api/test")
    public String test(){
        return "테스트 성공";
    }
}
