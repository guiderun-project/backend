package com.guide.run.global.jwt;

import com.guide.run.global.exception.auth.authorize.NotExistAuthorizationException;
import com.guide.run.global.exception.auth.authorize.NotValidRefreshTokenException;
import com.guide.run.global.redis.TmpToken;
import com.guide.run.global.redis.TmpTokenRepository;
import com.guide.run.global.security.user.CustomUserDetailsService;
import com.guide.run.global.redis.RefreshToken;
import com.guide.run.global.redis.RefreshTokenRepository;
import com.guide.run.user.entity.type.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsUtils;

import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {
    @Value("${spring.jwt.secretKey}")
    private String secretKey;
   // public static final long TOKEN_VALID_TIME = 1000L * 60 * 30 ; // 30분
    public static final long TOKEN_VALID_TIME = 1000L *60 *60 ;
    public static final long REFRESH_TOKEN_VALID_TIME = 1000L * 60 * 60 * 24 * 30; //한 달

    public static final long TMP_VALID_TIME = 1000L*60*30; //문자인증 후 주는 임시토큰 30분

    private static final String TOKEN_TYPE_CLAIM = "type";
    private static final String SIGNUP_TOKEN_TYPE = "signup";


    private final CustomUserDetailsService customUserDetailsService;
    private final RefreshTokenRepository refreshTokenRepository;

    private final TmpTokenRepository tmpTokenRepository;

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

    // 가입 전 사용자용 토큰. DB에 회원 row가 없어도 /api/signup을 호출할 수 있다.
    public String createSignupToken(String privateId){
        Claims claims = Jwts.claims().setSubject(privateId);
        claims.put(TOKEN_TYPE_CLAIM, SIGNUP_TOKEN_TYPE);
        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime()+TOKEN_VALID_TIME))
                .signWith(SignatureAlgorithm.HS256,secretKey)
                .compact();
    }

    public String createRefreshToken(String privateId){
        Optional<RefreshToken> oldRefreshToken = refreshTokenRepository.findByPrivateId(privateId);
        if(!oldRefreshToken.isEmpty()){
            refreshTokenRepository.delete(oldRefreshToken.get());
        }
        Date now = new Date();
       RefreshToken refreshToken= new RefreshToken(Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_VALID_TIME))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact(), privateId);
        refreshTokenRepository.save(refreshToken);
        return refreshToken.getToken();
    }
    public void deleteRefreshToken(RefreshToken refreshToken){
        refreshTokenRepository.delete(refreshToken);
        log.info("리프레시 토큰 제거 완료 refreshToken : "+refreshToken.getToken());
    }
    public Authentication getAuthentication(String token){
        Claims claims = parseClaims(token);
        if (SIGNUP_TOKEN_TYPE.equals(claims.get(TOKEN_TYPE_CLAIM, String.class))) {
            // 가입 전 사용자는 DB에 row가 없으므로 토큰만으로 ROLE_NEW 권한을 준다.
            return new UsernamePasswordAuthenticationToken(claims.getSubject(), "",
                    List.of(new SimpleGrantedAuthority(Role.ROLE_NEW.getValue())));
        }
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(claims.getSubject());
        return new UsernamePasswordAuthenticationToken(userDetails,"",userDetails.getAuthorities());
    }

    public String getSocialId(String token){
        return parseClaims(token).getSubject();
    }

    private Claims parseClaims(String token){
        try {
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            log.warn("만료된 JWT 토큰 사용 시도: subject={}", e.getClaims().getSubject());
            return e.getClaims();
        } catch (JwtException e) {
            log.error("유효하지 않은 JWT 토큰: {}", e.getMessage());
            throw e;
        }
    }

    public String extractUserId(HttpServletRequest httpServletRequest){
        String accessToken = resolveToken(httpServletRequest);
        return getSocialId(accessToken);
    }

    public String tryExtractUserId(HttpServletRequest request) {
        String bearer = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (bearer == null || !bearer.startsWith("Bearer ")) {
            return null;
        }
        return getSocialId(bearer.substring("Bearer ".length()));
    }

    public String resolveToken(HttpServletRequest request) {
        if (CorsUtils.isPreFlightRequest(request)) {
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

    public String getPrivateIdForCookie(Cookie[] cookies){
        for(Cookie cookie: cookies){
            if(cookie.getName().equals("refreshToken")){
                RefreshToken token = refreshTokenRepository.findById(cookie.getValue()).orElseThrow(() -> new NotValidRefreshTokenException());
                return token.getPrivateId();
            }
        }
        throw new NotValidRefreshTokenException();
    }

    public String createTmpToken(String authPhone, String privateId, String type){
        Claims claims = Jwts.claims().setSubject(authPhone);
        Date now = new Date();
        TmpToken token= new TmpToken(Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + TMP_VALID_TIME))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact(), privateId, type);
        tmpTokenRepository.save(token);
        return token.getToken();
    }

    public String getPrivateIdForRefreshToken(String refreshToken) {
        RefreshToken token = refreshTokenRepository.findById(refreshToken).orElseThrow(() -> new NotValidRefreshTokenException());
        return token.getPrivateId();
    }
}
