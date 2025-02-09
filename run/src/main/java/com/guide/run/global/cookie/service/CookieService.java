package com.guide.run.global.cookie.service;

import com.guide.run.global.jwt.JwtProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CookieService {
    private final JwtProvider jwtProvider;
    public void createCookie(String cookieName, HttpServletResponse response, String privateId) {
        int maxAge = 24 * 60 * 60 * 365;
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

    public void deleteOldCookieAndMakeNewCookie(HttpServletResponse response, Cookie cookie) {
        Cookie updatedCookie = new Cookie("refreshToken", cookie.getValue());
        updatedCookie.setPath("/"); // 새 경로 설정
        // 기존 쿠키의 설정을 그대로 반영 (만료시간, HttpOnly, Secure 등)
        updatedCookie.setMaxAge(cookie.getMaxAge());
        updatedCookie.setHttpOnly(cookie.isHttpOnly());
        updatedCookie.setSecure(cookie.getSecure());

        //기존 쿠키 삭제
        Cookie deleteCookie = new Cookie("refreshToken", null);
        deleteCookie.setPath("/api/oauth/login");
        deleteCookie.setHttpOnly(cookie.isHttpOnly());
        deleteCookie.setSecure(cookie.getSecure());
        deleteCookie.setMaxAge(0);

        response.addCookie(deleteCookie);
        response.addCookie(updatedCookie);
    }
}
