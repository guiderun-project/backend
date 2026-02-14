package com.guide.run.user.service;

import com.google.gson.Gson;
import com.guide.run.user.dto.request.OAuthRequest;
import com.guide.run.user.dto.response.OAuthCodeResponse;
import com.guide.run.user.factory.OAuthRequestFactory;
import com.guide.run.user.info.GetGoogleInfo;
import com.guide.run.user.info.GetKakaoInfo;
import com.guide.run.user.info.GetNaverInfo;
import com.guide.run.user.profile.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
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


    public OAuthProfile getProfile(String accessToken, String provider) throws CommunicationException {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        httpHeaders.set("Authorization", "Bearer " + accessToken);

        String profileUrl = oAuthRequestFactory.getProfileUrl(provider);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(null, httpHeaders);
        ResponseEntity<String> response = restTemplate.postForEntity(profileUrl, request, String.class);

        try {
            if (response.getStatusCode() == HttpStatus.OK) {
                return extractProfile(response, provider);
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
            log.info(getKakaoInfo.getId());
            return new KakaoProfile("kakao"+getKakaoInfo.getId(),"kakao");
        } else if(provider.equals("google")) {
            GetGoogleInfo getGoogleInfo = gson.fromJson(response.getBody(), GetGoogleInfo.class);
            log.info(getGoogleInfo.getSub());
            return new GoogleProfile("google"+getGoogleInfo.getSub(),"google");
        }else {
            GetNaverInfo getNaverInfo = gson.fromJson(response.getBody(), GetNaverInfo.class);
            log.info(getNaverInfo.getResponse().getId());
            return new NaverProfile("naver"+getNaverInfo.getResponse().getId(),"naver");
        }
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
