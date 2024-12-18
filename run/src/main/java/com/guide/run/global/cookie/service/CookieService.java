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
        ResponseCookie cookie = ResponseCookie.from(cookieName, jwtProvider.createRefreshToken(privateId))
                .domain(".guide-run-qa.netlify.app")
                .sameSite("None")
                .secure(true)
                .httpOnly(true)
                .build();


        response.setHeader("Set-Cookie", cookie.toString());
    }
}
