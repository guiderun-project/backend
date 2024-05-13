package com.guide.run.user.controller;

import com.guide.run.global.cookie.service.CookieService;
import com.guide.run.global.exception.auth.authorize.NotValidRefreshTokenException;
import com.guide.run.global.exception.user.dto.DuplicatedUserIdException;
import com.guide.run.global.jwt.JwtProvider;
import com.guide.run.user.dto.GuideSignupDto;
import com.guide.run.user.dto.ReissuedAccessTokenDto;
import com.guide.run.user.dto.ViSignupDto;
import com.guide.run.user.dto.request.AccountIdDto;
import com.guide.run.user.dto.request.GeneralLoginRequest;
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
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.CommunicationException;
import javax.security.auth.RefreshFailedException;

@CrossOrigin(origins = {"https://guide-run-qa.netlify.app", "https://guiderun.org",
        "https://guide-run.netlify.app","https://www.guiderun.org", "http://localhost:3000"},
maxAge = 3600,
allowCredentials = "true")

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
    public LoginResponse kakaoLogin(String code, HttpServletRequest request,HttpServletResponse response) throws CommunicationException {
        String accessToken = providerService.getAccessToken(code, "kakao").getAccess_token();
        OAuthProfile oAuthProfile = providerService.getProfile(accessToken,"kakao");
        String privateId = oAuthProfile.getSocialId();
        boolean isExist = userService.getUserStatus(privateId);

        boolean isExistCookie =false;


        if(request.getCookies() !=null){
            for(Cookie cookie: request.getCookies()){
                if(cookie.getName().equals("refreshToken")){
                    isExistCookie=true;
                }
            }
        }
        if(!isExistCookie) {
            cookieService.createCookie("refreshToken", response, privateId);
        }


        return LoginResponse.builder()
                .accessToken(jwtProvider.createAccessToken(privateId))
                .isExist(isExist)
                .build();
    }
    
    @PostMapping("/login")
    public LoginResponse generalLogin(@RequestBody GeneralLoginRequest request){
        log.info(request.getAccountId(), request.getPassword());

        String privateId = userService.generalLogin(request.getAccountId(), request.getPassword());
        boolean isExist = userService.getUserStatus(privateId);
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
    public ReissuedAccessTokenDto accessTokenReissue(HttpServletRequest request){
        try {
            Cookie[] cookies = request.getCookies();
            String privateId = jwtProvider.getPrivateIdForCookie(cookies);
            boolean isExist = userService.getUserStatus(privateId);
            return ReissuedAccessTokenDto.builder()
                    .accessToken(jwtProvider.createAccessToken(privateId))
                    .isExist(isExist)
                    .build();
        }catch (Exception e){
            log.error("refresh 토큰이 없습니다");
        }
        throw new NotValidRefreshTokenException();
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
