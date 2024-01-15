package com.guide.run.global.jwt;

import com.guide.run.global.security.user.CustomUserDetailsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;


@Component
@RequiredArgsConstructor
public class JwtProvider {
    @Value("${spring.jwt.secretKey}")
    private String secretKey;
    public static final long TOKEN_VALID_TIME = 1000L * 60 * 30 ; // 30분
    public static final long REFRESH_TOKEN_VALID_TIME = 1000L * 60 * 60 * 24 * 7; // 7일

    private final CustomUserDetailsService customUserDetailsService;

    @PostConstruct
    public void init(){
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createAccessToken(String socialId){
        Claims claims = Jwts.claims().setSubject(socialId);
        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime()+TOKEN_VALID_TIME))
                .signWith(SignatureAlgorithm.HS256,secretKey)
                .compact();
    }
    public String createRefreshToken(){
        Date now = new Date();

        return Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime()+REFRESH_TOKEN_VALID_TIME))
                .signWith(SignatureAlgorithm.HS256,secretKey)
                .compact();
    }
    public Authentication getAuthentication(String token){
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(getSocialId(token));
        return new UsernamePasswordAuthenticationToken(userDetails,"",userDetails.getAuthorities());
    }

    public String getSocialId(String token){
        try {
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
        }catch (ExpiredJwtException e){
            return e.getClaims().getSubject();
        }
    }

    public String extractUserId(HttpServletRequest httpServletRequest){
        String accessToken = resolveToken(httpServletRequest);
        return getSocialId(accessToken);
    }

    public String resolveToken(HttpServletRequest request){
        String bearer = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(bearer!=null && bearer.startsWith("Bearer "))
            return bearer.substring("Bearer ".length());
        return null ; //에러 작성해야함
    }

    public boolean validateTokenExpiration(String token){
        try{
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (Exception e){
            return false;
        }
    }
}
