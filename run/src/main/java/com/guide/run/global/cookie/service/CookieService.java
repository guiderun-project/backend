package com.guide.run.global.cookie.service;

import com.guide.run.global.jwt.JwtProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CookieService {
    private final JwtProvider jwtProvider;
    int maxAge = 24 * 60 * 60 * 30;
    public void createCookie(String cookieName, HttpServletResponse response, String privateId) {
        ResponseCookie cookie = ResponseCookie.from(cookieName, jwtProvider.createRefreshToken(privateId))
                .maxAge(maxAge)
                .sameSite("None")
                .path("/")
                .secure(true)
                .httpOnly(true)
                .build();


        response.setHeader("Set-Cookie", cookie.toString());
    }

    public void deleteRefreshTokenCookie(HttpServletResponse response) {
        Cookie deleteCookie = new Cookie("refreshToken", null);
        deleteCookie.setPath("/");
        deleteCookie.setHttpOnly(true);
        deleteCookie.setSecure(true);
        deleteCookie.setMaxAge(0);
        response.addCookie(deleteCookie);
    }

    public void deleteOldCookieAndMakeNewCookie(String privateId,HttpServletResponse response, Cookie cookie) {
        Cookie updatedCookie = new Cookie("refreshToken", cookie.getValue());
        updatedCookie.setPath("/"); // 새 경로 설정
        // 기존 쿠키의 설정을 그대로 반영 (만료시간, HttpOnly, Secure 등)
        updatedCookie.setMaxAge(maxAge);
        updatedCookie.setHttpOnly(true);
        updatedCookie.setSecure(true);
        /*Optional<RefreshToken> oldRefreshToken = refreshTokenRepository.findByPrivateId(privateId);
        if(!oldRefreshToken.isEmpty()){
            jwtProvider.deleteRefreshToken(oldRefreshToken.get());
        }*/

        //기존 쿠키 삭제
        Cookie deleteCookie = new Cookie("refreshToken", null);
        deleteCookie.setPath("/api/oauth/login");
        deleteCookie.setHttpOnly(true);
        deleteCookie.setSecure(true);
        deleteCookie.setMaxAge(0);

        response.addCookie(deleteCookie);
        response.addCookie(updatedCookie);
    }
}
