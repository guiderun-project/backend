package com.guide.run.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.guide.run.user.dto.request.AccountIdPhoneRequest;
import com.guide.run.user.dto.request.PhoneNumberRequest;
import com.guide.run.user.dto.response.FindAccountIdDto;
import com.guide.run.user.dto.response.TokenResponse;
import com.guide.run.user.service.LoginInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> getNumberForAccountId(@RequestBody PhoneNumberRequest request) throws UnsupportedEncodingException, URISyntaxException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
        loginInfoService.getNumberForAccountId(request.getPhoneNum());
        return ResponseEntity.ok("");
    }

    //인증번호 요청(비밀번호 재설정)
    @PostMapping("/sms/password")
    public ResponseEntity<String> getNumberForPassword(@RequestBody AccountIdPhoneRequest request)
            throws UnsupportedEncodingException, URISyntaxException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
        loginInfoService.getNumberForPassword(request);
        return ResponseEntity.ok("");
    }

    //인증번호 확인(아이디 찾기, 비밀번호 재설정용 토큰 발급)
    @PostMapping("/sms/token")
    public ResponseEntity<TokenResponse> getToken(@RequestParam String number){
        return ResponseEntity.ok(loginInfoService.getToken(number));
    }

    //아이디 찾기
    @GetMapping("/accountId")
    public ResponseEntity<FindAccountIdDto> findAccountId(@RequestParam String token){
        return ResponseEntity.ok(loginInfoService.findAccountId(token));
    }
    //비밀번호 재설정
    @PostMapping("/new-password")
    public ResponseEntity<String> createNewPassword(@RequestParam String token, @RequestParam String newPassword){
        loginInfoService.createNewPassword(token,newPassword);
        return ResponseEntity.ok("");
    }

}
