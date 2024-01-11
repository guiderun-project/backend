package com.guide.run.user.controller;

import com.guide.run.global.cookie.service.CookieService;
import com.guide.run.global.jwt.JwtProvider;
import com.guide.run.user.dto.ViSignupDto;
import com.guide.run.user.entity.*;
import com.guide.run.user.repository.PartnerRepository;
import com.guide.run.user.repository.UserRepository;
import com.guide.run.user.response.LoginResponse;
import com.guide.run.user.profile.OAuthProfile;
import com.guide.run.user.service.ProviderService;
import com.guide.run.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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
    private final UserRepository userRepository;
    private final PartnerRepository partnerRepository;


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

    @PostMapping("/api/signup/vi")
    public User viSignup(@RequestBody ViSignupDto viSignupDto, HttpServletRequest httpServletRequest){
        String accessToken = jwtProvider.resolveToken(httpServletRequest);
        String socialId = jwtProvider.getSocialId(accessToken);
        return userService.viSignup(socialId, viSignupDto);
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

    //테스트입니다
    @PostMapping("/api")
    public void abc(){
        Vi vi1 = Vi.builder()
                .socialId("aa_1")
                .role(Role.VI)
                .build();
        Vi vi2 = Vi.builder()
                .socialId("aa_2")
                .role(Role.VI)
                .build();
        Guide guide1 = Guide.builder()
                .socialId("gg_1")
                .role(Role.GUIDE)
                .build();
        Guide guide2 = Guide.builder()
                .socialId("gg_2")
                .role(Role.GUIDE)
                .build();
        Guide guide3 = Guide.builder()
                .socialId("gg_3")
                .role(Role.GUIDE)
                .build();
        userRepository.save(vi1);
        userRepository.save(vi2);
        userRepository.save(guide1);
        userRepository.save(guide2);
        userRepository.save(guide3);

        partnerRepository.save(Partner.builder()
                .viId(vi1)
                .guideId(guide1)
                .build());
        partnerRepository.save(Partner.builder()
                .viId(vi1)
                .guideId(guide2)
                .build());
        partnerRepository.save(Partner.builder()
                .viId(vi2)
                .guideId(guide2)
                .build());
        partnerRepository.save(Partner.builder()
                .viId(vi2)
                .guideId(guide3)
                .build());
        PartnerId partnerId = new PartnerId("aa_1", "gg_1");
        Partner pa = partnerRepository.findById(partnerId).orElse(null);
        if(pa!=null){
            partnerRepository.save(Partner.builder()
                    .viId(pa.getViId())
                    .guideId(pa.getGuideId())
                    .trainingCnt(pa.getTrainingCnt()+1)
                    .competitionCnt(pa.getCompetitionCnt())
                    .build()
            );
        }

    }
}
