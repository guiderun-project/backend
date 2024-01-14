package com.guide.run.user.controller;

import com.guide.run.global.cookie.service.CookieService;
import com.guide.run.global.jwt.JwtProvider;
import com.guide.run.user.dto.GuideSignupDto;
import com.guide.run.user.dto.PermissionDto;
import com.guide.run.user.dto.ViSignupDto;
import com.guide.run.user.dto.response.LoginResponse;
import com.guide.run.user.dto.response.SignupResponse;
import com.guide.run.user.profile.OAuthProfile;
import com.guide.run.user.service.MyPageService;
import com.guide.run.user.service.ProviderService;
import com.guide.run.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.naming.CommunicationException;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SignController {
    private final ProviderService providerService;
    private final JwtProvider jwtProvider;
    private final CookieService cookieService;
    private final UserService userService;

    private final MyPageService myPageService;


    @PostMapping("/oauth/login/kakao")
    public LoginResponse kakaoLogin(String code, HttpServletResponse response) throws CommunicationException {
        String accessToken = providerService.getAccessToken(code, "kakao").getAccess_token();
        OAuthProfile oAuthProfile = providerService.getProfile(accessToken,"kakao");
        String userId = oAuthProfile.getSocialId();
        String userStatus = userService.getUserStatus(userId);

        cookieService.createCookie("refreshToken",response);

        return LoginResponse.builder()
                .accessToken(jwtProvider.createAccessToken(userId))
                .userStatus(userStatus)
                .build();
    }

    @Secured("ROLE_NEW")
    @PostMapping("/signup/vi")
    public ResponseEntity<SignupResponse> viSignup(@RequestBody ViSignupDto viSignupDto, HttpServletRequest httpServletRequest){
        String userId = extractUserId(httpServletRequest);
        SignupResponse response = userService.viSignup(userId, viSignupDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Secured("ROLE_NEW")
    @PostMapping("/signup/guide")
    public ResponseEntity<SignupResponse> guideSignup(@RequestBody GuideSignupDto guideSignupDto, HttpServletRequest httpServletRequest){
        String userId = extractUserId(httpServletRequest);
        SignupResponse response = userService.guideSignup(userId, guideSignupDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/mypage/info/permission")
    public ResponseEntity<PermissionDto> getPermission(HttpServletRequest httpServletRequest){
        String userId = extractUserId(httpServletRequest);
        PermissionDto response = myPageService.getPermission(userId);

        return ResponseEntity.ok().body(response);
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


    public String extractUserId(HttpServletRequest httpServletRequest){
        String accessToken = jwtProvider.resolveToken(httpServletRequest);
        return jwtProvider.getSocialId(accessToken);
    }
}
