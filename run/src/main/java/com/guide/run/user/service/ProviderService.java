package com.guide.run.user.service;

import com.google.gson.Gson;
import com.guide.run.user.request.OAuthRequest;
import com.guide.run.user.response.OAuthCodeResponse;
import com.guide.run.user.factory.OAuthRequestFactory;
import com.guide.run.user.entity.Role;
import com.guide.run.user.entity.User;
import com.guide.run.user.info.GetGoogleInfo;
import com.guide.run.user.info.GetKakaoInfo;
import com.guide.run.user.profile.*;
import com.guide.run.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.naming.CommunicationException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProviderService {

    private final RestTemplate restTemplate;
    private final Gson gson;
    private final OAuthRequestFactory oAuthRequestFactory;
    private final UserRepository userRepository;

    @Transactional
    public String socialSignup(OAuthProfile oAuthProfile){
        String socialId = oAuthProfile.getSocialId();

        User user = User.builder()
                .socialId(socialId)
                .role(Role.User)
                .build();
        userRepository.save(user);

        return socialId;
        //socialId값을 리턴할지 ???? id 값을 리턴할지???
        //User saved = userRepository.save(user);
        //return saved.getId();

    }

    public OAuthProfile getProfile(String accessToken, String provider) throws CommunicationException {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        httpHeaders.set("Authorization", "Bearer " + accessToken);

        String profileUrl = oAuthRequestFactory.getProfileUrl(provider);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(null, httpHeaders);
        ResponseEntity<String> response = restTemplate.postForEntity(profileUrl, request, String.class);

        try {
            if (response.getStatusCode() == HttpStatus.OK) {
                OAuthProfile oAuthProfile = extractProfile(response, provider);
                return oAuthProfile;
            }
        } catch (Exception e) {
            throw new CommunicationException();
        }
        throw new CommunicationException();
    }

    //getXXXinfo형태로 데이터를 받아와서 필요한 정보(만 뽑아 XXXprofile로 구성해 내보내준다
    private OAuthProfile extractProfile(ResponseEntity<String> response, String provider) {
        if (provider.equals("kakao")) {
            GetKakaoInfo getKakaoInfo = gson.fromJson(response.getBody(), GetKakaoInfo.class);
            KakaoProfile kakaoProfile = new KakaoProfile("kakao_"+getKakaoInfo.getId(),"kakao");
            return kakaoProfile;
        } else if(provider.equals("google")) {
            GetGoogleInfo getGoogleInfo = gson.fromJson(response.getBody(), GetGoogleInfo.class);
            GoogleProfile googleProfile = new GoogleProfile("google_"+getGoogleInfo.getSub(),"google");
            return googleProfile;
        }
        return null; // 에러 추가 바람
    }

    public OAuthCodeResponse getAccessToken(String code, String provider) throws CommunicationException {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        OAuthRequest oAuthRequest = oAuthRequestFactory.getRequest(code, provider);
        HttpEntity<LinkedMultiValueMap<String, String>> request = new HttpEntity<>(oAuthRequest.getMap(), httpHeaders);

        ResponseEntity<String> response = restTemplate.postForEntity(oAuthRequest.getUrl(), request, String.class);
        try {
            if (response.getStatusCode() == HttpStatus.OK) {
                return gson.fromJson(response.getBody(), OAuthCodeResponse.class);
            }
        } catch (Exception e) {
            throw new CommunicationException();
        }
        throw new CommunicationException();
    }


}
