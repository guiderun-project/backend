package com.guide.run.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.guide.run.user.dto.request.AccountIdPhoneRequest;
import com.guide.run.user.dto.request.AuthNumRequest;
import com.guide.run.user.dto.request.NewPasswordDto;
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

@CrossOrigin(origins = {"https://guide-run-qa.netlify.app", "https://guiderun.org",
        "https://guide-run.netlify.app","https://www.guiderun.org", "http://localhost:3000", "http://localhost:8080"},
        maxAge = 3600,
        allowCredentials = "true")
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
    public ResponseEntity<TokenResponse> getToken(@RequestBody AuthNumRequest request){
        return ResponseEntity.ok(loginInfoService.getToken(request.getNumber()));
    }

    //아이디 찾기
    @PostMapping("/accountId")
    public ResponseEntity<FindAccountIdDto> findAccountId(@RequestBody String token){
        return ResponseEntity.ok(loginInfoService.findAccountId(token));
    }
    //비밀번호 재설정
    @PatchMapping("/new-password")
    public ResponseEntity<String> createNewPassword(@RequestBody NewPasswordDto newPasswordDto){
        loginInfoService.createNewPassword(newPasswordDto.getToken(),newPasswordDto.getNewPassword());
        return ResponseEntity.ok("");
    }

}
