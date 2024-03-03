package com.guide.run.global.jwt;

import com.guide.run.global.exception.auth.authorize.NotExistAuthorizationException;
import com.guide.run.global.exception.auth.authorize.NotValidRefreshTokenException;
import com.guide.run.global.security.user.CustomUserDetailsService;
import com.guide.run.global.redis.RefreshToken;
import com.guide.run.global.redis.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
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
   // public static final long TOKEN_VALID_TIME = 1000L * 60 * 30 ; // 30분
    public static final long TOKEN_VALID_TIME = 1000L *60 *60  ;
    public static final long REFRESH_TOKEN_VALID_TIME = 1000L * 60 * 60 * 24 * 365; // 365일

    private final CustomUserDetailsService customUserDetailsService;
    private final RefreshTokenRepository refreshTokenRepository;

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
    public String createRefreshToken(String privateId){
        Date now = new Date();
       RefreshToken refreshToken= new RefreshToken(Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_VALID_TIME))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact(), privateId);
        refreshTokenRepository.save(refreshToken);
        return refreshToken.getToken();
    }
    public Authentication getAuthentication(String token){
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(getSocialId(token));
        return new UsernamePasswordAuthenticationToken(userDetails,"",userDetails.getAuthorities());
    }

    public String getSocialId(String token){
        try {
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
        }catch (ExpiredJwtException e){
            System.out.println(e.getClaims().getSubject()+"-----------------------------");
            return e.getClaims().getSubject();
        }
    }

    public String extractUserId(HttpServletRequest httpServletRequest){
        String accessToken = resolveToken(httpServletRequest);
        return getSocialId(accessToken);
    }

    public String resolveToken(HttpServletRequest request) {
        if (request.getMethod().equals("OPTIONS")) {
            return null;
        }else {
            String bearer = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (bearer != null && bearer.startsWith("Bearer "))
                return bearer.substring("Bearer ".length());
            throw new NotExistAuthorizationException("인증 에러");
        }
    }

    public boolean validateTokenExpiration(String token){
        try{
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (Exception e){
            return false;
        }
    }

    public String reissue(Cookie[] cookies){
        for(Cookie cookie: cookies){
            if(cookie.getName().equals("refreshToken")){
                RefreshToken token = refreshTokenRepository.findById(cookie.getValue()).orElseThrow(() -> new NotValidRefreshTokenException());
                return createAccessToken(token.getPrivateId());
            }
        }
        throw new NotValidRefreshTokenException();
    }
}
