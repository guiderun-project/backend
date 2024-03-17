package com.guide.run.user.controller;

import com.guide.run.global.cookie.service.CookieService;
import com.guide.run.global.exception.auth.authorize.NotValidRefreshTokenException;
import com.guide.run.global.exception.user.dto.DuplicatedUserIdException;
import com.guide.run.global.jwt.JwtProvider;
import com.guide.run.global.redis.RefreshToken;
import com.guide.run.global.redis.RefreshTokenRepository;
import com.guide.run.user.dto.GuideSignupDto;
import com.guide.run.user.dto.ReissuedAccessTokenDto;
import com.guide.run.user.dto.ViSignupDto;
import com.guide.run.user.dto.request.AccountIdDto;
import com.guide.run.user.dto.response.IsDuplicatedResponse;
import com.guide.run.user.dto.response.LoginResponse;
import com.guide.run.user.dto.response.SignupResponse;
import com.guide.run.user.profile.OAuthProfile;
import com.guide.run.user.service.GuideService;
import com.guide.run.user.service.ProviderService;
import com.guide.run.user.service.UserService;
import com.guide.run.user.service.ViService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.CommunicationException;
@CrossOrigin(origins = {"https://guide-run-qa.netlify.app", "https://guiderun.org",
        "https://guide-run.netlify.app","https://www.guiderun.org", "http://localhost:3000"},
maxAge = 3600, allowCredentials = "true")

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
    private final RefreshTokenRepository refreshTokenRepository;


    @PostMapping("/oauth/login/kakao")
    public LoginResponse kakaoLogin(String code, HttpServletRequest request,HttpServletResponse response) throws CommunicationException {
        String accessToken = providerService.getAccessToken(code, "kakao").getAccess_token();
        OAuthProfile oAuthProfile = providerService.getProfile(accessToken,"kakao");
        String privateId = oAuthProfile.getSocialId();
        boolean isExist = userService.getUserStatus(privateId);

        Cookie[] cookies = request.getCookies();
        if(cookies==null) {
            cookieService.createCookie("refreshToken", response, privateId);
        }

        return LoginResponse.builder()
                .accessToken(jwtProvider.createAccessToken(privateId))
                .isExist(isExist)
                .build();
    }
    @PostMapping("/signup/vi")
    public ResponseEntity<SignupResponse> viSignup(@RequestBody @Valid ViSignupDto viSignupDto, HttpServletRequest httpServletRequest){
        String userId = jwtProvider.extractUserId(httpServletRequest);
        if(userService.isAccountIdExist(viSignupDto.getAccountId())){
            throw new DuplicatedUserIdException();
        }else{
            SignupResponse response = viService.viSignup(userId, viSignupDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
    }


    @PostMapping("/signup/guide")
    public ResponseEntity<SignupResponse> guideSignup(@RequestBody @Valid GuideSignupDto guideSignupDto, HttpServletRequest httpServletRequest){
        String userId = jwtProvider.extractUserId(httpServletRequest);
        if(userService.isAccountIdExist(guideSignupDto.getAccountId())){
            throw new DuplicatedUserIdException();
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
    @GetMapping("/oauth/login/reissue")
    public ReissuedAccessTokenDto accessTokenReissue(HttpServletRequest request) throws CommunicationException {
        String privateId = jwtProvider.getPrivateIdForCookie(request.getCookies());
        String accessToken = jwtProvider.createAccessToken(privateId);
        boolean isExist = userService.getUserStatus(privateId);
        return ReissuedAccessTokenDto.builder()
                .accessToken(accessToken)
                .isExist(isExist)
                .build();
    }

    //아이디 중복확인
    @PostMapping("/signup/duplicated")
    public ResponseEntity<IsDuplicatedResponse> isIdDuplicated(@RequestBody AccountIdDto aa){
        IsDuplicatedResponse response =
                IsDuplicatedResponse.builder()
                        .isUnique(!userService.isAccountIdExist(aa.getAccountId()))
                        .build();
        return ResponseEntity.ok().body(response);
    }

}
