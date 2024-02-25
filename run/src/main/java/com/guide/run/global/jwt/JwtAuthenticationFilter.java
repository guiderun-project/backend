package com.guide.run.global.jwt;

import com.guide.run.global.exception.auth.authorize.NotValidAccessTokenException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {
    private final JwtProvider jwtProvider;
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = jwtProvider.resolveToken((HttpServletRequest) request);
        if(token != null){
            if(jwtProvider.validateTokenExpiration(token)) {
                Authentication authentication = jwtProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            else
                throw new NotValidAccessTokenException();
        }
        chain.doFilter(request,response);
    }
}
