package com.guide.run.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.guide.run.event.service.EventService;
import com.guide.run.global.exception.auth.authorize.NotValidRefreshTokenException;
import com.guide.run.global.jwt.JwtProvider;
import com.guide.run.global.redis.RefreshTokenRepository;
import com.guide.run.user.dto.request.*;
import com.guide.run.user.dto.response.FindAccountIdDto;
import com.guide.run.user.dto.response.TokenResponse;
import com.guide.run.user.service.LoginInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


@CrossOrigin(origins = {"https://dev.guiderun.org", "https://guiderun.org","https://www.guiderun.org", "http://localhost:3000", "http://localhost:8080"},
        maxAge = 3600,
        allowCredentials = "true")
@Tag(name = "Auth", description = "아이디 찾기, 비밀번호 재설정, 로그아웃과 관련된 인증 보조 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LoginInfoController {
    private final LoginInfoService loginInfoService;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private static final Logger log = LoggerFactory.getLogger(EventService.class);

    //인증번호 요청(아이디 찾기)
    @Operation(summary = "아이디 찾기용 인증번호 요청", description = "전화번호만으로 아이디 찾기 인증번호를 발송합니다.", security = {})
    @PostMapping("/sms/accountId")
    public ResponseEntity<String> getNumberForAccountId(@RequestBody PhoneNumberRequest request) throws UnsupportedEncodingException, URISyntaxException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
        loginInfoService.getNumberForAccountId(request.getPhoneNum());
        return ResponseEntity.ok("");
    }

    //인증번호 요청(비밀번호 재설정)
    @Operation(summary = "비밀번호 재설정용 인증번호 요청", description = "accountId와 전화번호를 함께 받아 비밀번호 재설정용 인증번호를 발송합니다.", security = {})
    @PostMapping("/sms/password")
    public ResponseEntity<String> getNumberForPassword(@RequestBody AccountIdPhoneRequest request)
            throws UnsupportedEncodingException, URISyntaxException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
        loginInfoService.getNumberForPassword(request);
        return ResponseEntity.ok("");
    }

    //인증번호 확인(아이디 찾기, 비밀번호 재설정용 토큰 발급)
    @Operation(summary = "인증번호 검증", description = "문자로 받은 인증번호를 검증하고 아이디 찾기 또는 비밀번호 재설정에 사용할 임시 토큰을 발급합니다.", security = {})
    @PostMapping("/sms/token")
    public ResponseEntity<TokenResponse> getToken(@RequestBody AuthNumRequest request){
        return ResponseEntity.ok(loginInfoService.getToken(request.getNumber()));
    }

    //아이디 찾기
    @Operation(summary = "아이디 찾기", description = "임시 토큰으로 계정 ID와 계정 생성일을 조회합니다.", security = {})
    @PostMapping("/accountId")
    public ResponseEntity<FindAccountIdDto> findAccountId(@RequestBody TmpTokenDto tmpTokenDto){
        return ResponseEntity.ok(loginInfoService.findAccountId(tmpTokenDto.getToken()));
    }
    //비밀번호 재설정
    @Operation(summary = "비밀번호 재설정", description = "임시 토큰을 사용해 새 비밀번호를 저장합니다.", security = {})
    @PatchMapping("/new-password")
    public ResponseEntity<String> createNewPassword(@RequestBody NewPasswordDto newPasswordDto){
        loginInfoService.createNewPassword(newPasswordDto.getToken(),newPasswordDto.getNewPassword());
        return ResponseEntity.ok("");
    }
    @Operation(summary = "로그아웃", description = "refreshToken 쿠키를 삭제해 로그아웃합니다. 프론트에서는 로그인된 마이페이지에서 호출합니다.", security = {})
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response){

        if(request.getCookies() !=null){
            for(Cookie cookie: request.getCookies()){
                if(cookie.getName().equals("refreshToken")){
                    refreshTokenRepository.deleteById(cookie.getValue());
                    log.info(cookie.getValue());
                    Cookie removedCookie = new Cookie("refreshToken", null);
                    removedCookie.setPath("/");
                    removedCookie.setHttpOnly(true);
                    removedCookie.setSecure(true);
                    removedCookie.setMaxAge(0);
                    response.addCookie(removedCookie);
                }
            }
        }else {
            log.error("refresh 토큰이 없습니다. privateId :" + jwtProvider.extractUserId(request));
            throw new NotValidRefreshTokenException();
        }

        return ResponseEntity.ok("");
    }

}
