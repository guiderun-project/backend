package com.guide.run.user.controller;

import com.guide.run.gloabl.jwt.JwtProvider;
import com.guide.run.user.entity.User;
import com.guide.run.user.repository.UserRepository;
import com.guide.run.user.response.LoginResponse;
import com.guide.run.user.response.OAuthCodeResponse;
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
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    @PostMapping("/kakao")
    public OAuthCodeResponse getKakaoAccessToken(String code) throws CommunicationException {
        return providerService.getAccessToken(code,"kakao");
    }
    @PostMapping("/login/kakao")
    public LoginResponse kakaoSignup(String code, HttpServletResponse response) throws CommunicationException {
        OAuthProfile oAuthProfile = providerService.getProfile(code,"kakao");
        User findUser = userRepository.findBySocialId(oAuthProfile.getSocialId()).orElse(null);
        String socialId;
        if(findUser == null){
            socialId = providerService.socialSignup(oAuthProfile);
        }
        else{
            socialId = findUser.getSocialId();
        }

        Cookie cookie = new Cookie("refresh",jwtProvider.createRefreshToken());
        cookie.setHttpOnly(true);
        response.addCookie(cookie);

        return LoginResponse.builder().
                socialId(socialId)
                .accessToken(jwtProvider.createAccessToken(socialId))
                .build();
    }
    @PostMapping("/google")
    public OAuthCodeResponse getGoogleAccessToken(String code) throws CommunicationException {
        return providerService.getAccessToken(code,"google");
    }
    @PostMapping("/login/google")
    public LoginResponse googleSignup(String code,HttpServletResponse response) throws CommunicationException {
        OAuthProfile oAuthProfile = providerService.getProfile(code,"google");
        User findUser = userRepository.findBySocialId(oAuthProfile.getSocialId()).orElse(null);
        String socialId;
        if(findUser == null){
            socialId = providerService.socialSignup(oAuthProfile);
        }
        else{
            socialId = findUser.getSocialId();
        }

        Cookie cookie = new Cookie("refresh",jwtProvider.createRefreshToken());
        cookie.setHttpOnly(true);
        response.addCookie(cookie);

        return LoginResponse.builder().
                socialId(socialId)
                .accessToken(jwtProvider.createAccessToken(socialId))
                .build();
    }

    @GetMapping("/api/test")
    public String test(){
        return "테스트 성공";
    }
}
