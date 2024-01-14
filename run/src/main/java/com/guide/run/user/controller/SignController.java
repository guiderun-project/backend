package com.guide.run.user.controller;

import com.guide.run.global.cookie.service.CookieService;
import com.guide.run.global.jwt.JwtProvider;
import com.guide.run.user.dto.ViSignupDto;
import com.guide.run.user.entity.*;
import com.guide.run.user.repository.PartnerRepository;
import com.guide.run.user.repository.UserRepository;
import com.guide.run.user.response.LoginResponse;
import com.guide.run.user.profile.OAuthProfile;
import com.guide.run.user.response.SignupResponse;
import com.guide.run.user.service.ProviderService;
import com.guide.run.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
        String userStatus = userService.getUserStatus(socialId);

        cookieService.createCookie("refreshToken",response);

        return LoginResponse.builder()
                .accessToken(jwtProvider.createAccessToken(socialId))
                .userStatus(userStatus)
                .build();
    }

    @Secured("ROLE_NEW")
    //정보 입력이 완료되면 임시 토큰이 아닌 accessToken 발급
    @PostMapping("/api/signup/vi")
    public SignupResponse viSignup(@RequestBody ViSignupDto viSignupDto, HttpServletRequest httpServletRequest){
        String accessToken = jwtProvider.resolveToken(httpServletRequest);
        String socialId = jwtProvider.getSocialId(accessToken);
        User signedUser = userService.viSignup(socialId, viSignupDto);
        return SignupResponse.builder() //socialId값 말고 UUID값 생기면 UUID값도 response에 넣어줘야 할 듯 합니다
                .accessToken(jwtProvider.createAccessToken(signedUser.getSocialId()))
                .userStatus(Role.VWAIT.getValue())
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
