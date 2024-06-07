package com.guide.run.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.guide.run.user.dto.request.AccountIdPhoneRequest;
import com.guide.run.user.dto.request.PhoneNumberRequest;
import com.guide.run.user.service.LoginInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LoginInfoController {
    private final LoginInfoService loginInfoService;

    //인증번호 요청(아이디 찾기)
    @PostMapping("/sms/accountId")
    public void getNumberForAccountId(@RequestBody PhoneNumberRequest request) throws UnsupportedEncodingException, URISyntaxException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
        loginInfoService.getNumberForAccountId(request.getPhoneNum());
    }

    //인증번호 요청(비밀번호 재설정)
    @PostMapping("/sms/password")
    public void getNumberForPassword(@RequestBody AccountIdPhoneRequest request)
            throws UnsupportedEncodingException, URISyntaxException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
        loginInfoService.getNumberForPassword(request);
    }

    //인증번호 확인(아이디 찾기, 비밀번호 재설정용 토큰 발급)
    @PostMapping("/sms/token")
    public void getToken(){

    }

    //아이디 찾기
    @PostMapping("/accountId")
    public void findAccountId(@RequestParam String token){

    }
    //비밀번호 재설정
    @PostMapping("/new-password")
    public void createNewPassword(@RequestParam String token, @RequestParam String newPassword){

    }


    //전화번호-인증번호 로 레디스 저장
    //이후 인증번호 확인 후 번호 지우고 임시토큰과 privateId를 가지는 정보를 다시 저장.
    //이후 레디스 확인해서 privateId 뽑아오고 해당 정보를 변경 가능하게 해줌.
}
