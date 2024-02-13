package com.guide.run.user.controller;

import com.guide.run.global.cookie.service.CookieService;
import com.guide.run.global.jwt.JwtProvider;
import com.guide.run.user.dto.GuideSignupDto;
import com.guide.run.user.dto.ViSignupDto;
import com.guide.run.user.dto.response.LoginResponse;
import com.guide.run.user.dto.response.SignupResponse;
import com.guide.run.user.profile.OAuthProfile;
import com.guide.run.user.service.GuideService;
import com.guide.run.user.service.ProviderService;
import com.guide.run.user.service.UserService;
import com.guide.run.user.service.ViService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.CommunicationException;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SignController {
    private final ProviderService providerService;
    private final JwtProvider jwtProvider;
    private final CookieService cookieService;
    private final UserService userService;

    private final ViService viService;
    private final GuideService guideService;


    @PostMapping("/oauth/login/kakao")
    public LoginResponse kakaoLogin(String code, HttpServletResponse response) throws CommunicationException {
        String accessToken = providerService.getAccessToken(code, "kakao").getAccess_token();
        OAuthProfile oAuthProfile = providerService.getProfile(accessToken,"kakao");
        String privateId = oAuthProfile.getSocialId();
        String userStatus = userService.getUserStatus(privateId);

        cookieService.createCookie("refreshToken",response);

        return LoginResponse.builder()
                .accessToken(jwtProvider.createAccessToken(privateId))
                .userStatus(userStatus)
                .build();
    }


    @PostMapping("/signup/vi")
    public ResponseEntity<SignupResponse> viSignup(@RequestBody @Valid ViSignupDto viSignupDto, HttpServletRequest httpServletRequest){
        String userId = jwtProvider.extractUserId(httpServletRequest);
        if(userService.isAccountIdExist(viSignupDto.getAccountId())){
            throw new RuntimeException("중복된 아이디입니다.");
        }else{
            SignupResponse response = viService.viSignup(userId, viSignupDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
    }


    @PostMapping("/signup/guide")
    public ResponseEntity<SignupResponse> guideSignup(@RequestBody @Valid GuideSignupDto guideSignupDto, HttpServletRequest httpServletRequest){
        String userId = jwtProvider.extractUserId(httpServletRequest);
        if(userService.isAccountIdExist(guideSignupDto.getAccountId())){
            throw new RuntimeException("중복된 아이디입니다.");
        }else{
            SignupResponse response = guideService.guideSignup(userId, guideSignupDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
    }


    @PostMapping("/oauth/token/google")
    public LoginResponse googleSignup(String code,HttpServletResponse response) throws CommunicationException {
        String accessToken = providerService.getAccessToken(code, "google").getAccess_token();
        OAuthProfile oAuthProfile = providerService.getProfile(accessToken,"google");
        String userId = oAuthProfile.getSocialId();

        return LoginResponse.builder()
                .accessToken(jwtProvider.createAccessToken(userId))
                .build();
    }

}
