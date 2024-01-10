package com.guide.run.global.cookie.service;

import com.guide.run.global.jwt.JwtProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CookieService {
    private final JwtProvider jwtProvider;
    public void createCookie(String cookieName, HttpServletResponse response) {
        Cookie cookie = new Cookie(cookieName, jwtProvider.createRefreshToken());
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }
}
