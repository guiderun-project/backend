package com.guide.run.user.controller;

import com.guide.run.global.cookie.service.CookieService;
import com.guide.run.global.exception.auth.authorize.NotValidRefreshTokenException;
import com.guide.run.global.jwt.JwtProvider;
import com.guide.run.user.dto.request.GeneralLoginRequest;
import com.guide.run.user.dto.request.SignupRequest;
import com.guide.run.user.dto.request.WithdrawalRequest;
import com.guide.run.user.dto.response.IntegratedSignupResponse;
import com.guide.run.user.dto.response.KakaoOAuthLoginResponse;
import com.guide.run.user.dto.response.LoginPostResponse;
import com.guide.run.user.entity.user.User;
import com.guide.run.user.profile.OAuthProfile;
import com.guide.run.user.service.ProviderService;
import com.guide.run.user.service.SignupService;
import com.guide.run.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.CommunicationException;

@Slf4j
@Tag(name = "Auth", description = "로그인, 회원가입, 토큰 재발급과 회원 탈퇴를 다루는 인증 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SignController {
    private final ProviderService providerService;
    private final JwtProvider jwtProvider;
    private final CookieService cookieService;
    private final UserService userService;
    private final SignupService signupService;


    @Operation(summary = "일반 로그인", description = "로그인 화면에서 계정 ID와 비밀번호로 로그인합니다. 응답 본문에는 accessToken을, HttpOnly Cookie에는 refreshToken을 내려줍니다.", security = {})
    @PostMapping("/login")
    public ResponseEntity<LoginPostResponse> generalLogin(@RequestBody GeneralLoginRequest request,
                                                          HttpServletRequest httpServletRequest,
                                                          HttpServletResponse httpServletResponse) {
        String privateId = userService.generalLogin(request.getAccountId(), request.getPassword());

        boolean isExistCookie = false;
        if (httpServletRequest.getCookies() != null) {
            for (Cookie cookie : httpServletRequest.getCookies()) {
                if (cookie.getName().equals("refreshToken")) {
                    cookieService.deleteOldCookieAndMakeNewCookie(privateId, httpServletResponse, cookie);
                    isExistCookie = true;
                }
            }
        }
        if (!isExistCookie) {
            cookieService.createCookie("refreshToken", httpServletResponse, privateId);
        }

        return ResponseEntity.ok(LoginPostResponse.builder()
                .accessToken(jwtProvider.createAccessToken(privateId))
                .build());
    }

    @Operation(summary = "카카오 OAuth 로그인", description = "카카오 인가 코드로 로그인합니다. 기존 회원은 LOGIN_SUCCESS와 accessToken(body) + refreshToken(Cookie)을, 신규 회원은 SIGNUP_REQUIRED와 signupToken을 반환합니다.", security = {})
    @PostMapping("/oauth/login/kakao")
    public ResponseEntity<KakaoOAuthLoginResponse> kakaoLogin(@RequestParam("code") String code,
                                                              HttpServletRequest request,
                                                              HttpServletResponse response) throws CommunicationException {
        String kakaoAccessToken = providerService.getAccessToken(code, "kakao").getAccess_token();
        OAuthProfile oAuthProfile = providerService.getProfile(kakaoAccessToken, "kakao");
        String privateId = oAuthProfile.getSocialId();
        String status = userService.getUserStatus(privateId);

        if ("0".equals(status)) {
            // 신규 회원 또는 미완성 회원 → 회원가입 플로우
            String signupToken = jwtProvider.createAccessToken(privateId);
            return ResponseEntity.ok(KakaoOAuthLoginResponse.builder()
                    .status("SIGNUP_REQUIRED")
                    .signupToken(signupToken)
                    .provider("KAKAO")
                    .build());
        }

        // 기존 회원 → refreshToken을 HttpOnly Cookie로, accessToken을 body로
        boolean isExistCookie = false;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("refreshToken")) {
                    cookieService.deleteOldCookieAndMakeNewCookie(privateId, response, cookie);
                    isExistCookie = true;
                }
            }
        }
        if (!isExistCookie) {
            cookieService.createCookie("refreshToken", response, privateId);
        }

        User user = userService.findByPrivateId(privateId);
        return ResponseEntity.ok(KakaoOAuthLoginResponse.builder()
                .status("LOGIN_SUCCESS")
                .accessToken(jwtProvider.createAccessToken(privateId))
                .user(KakaoOAuthLoginResponse.UserInfo.builder()
                        .userId(user.getUserId())
                        .role(user.getRole())
                        .disabilityType(user.getType())
                        .build())
                .build());
    }

    @Operation(summary = "통합 회원가입 완료", description = "소셜 로그인 후 NEW 권한 사용자가 disabilityType(VI/GUIDE)에 따라 통합 회원가입 폼을 제출합니다. common 기본 정보와 vi/guide 전용 정보, 약관 동의를 함께 받으며, accessToken/refreshToken을 발급합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/signup")
    public ResponseEntity<IntegratedSignupResponse> signup(@RequestBody @Valid SignupRequest request,
                                                           HttpServletRequest httpServletRequest){
        String privateId = jwtProvider.extractUserId(httpServletRequest);
        IntegratedSignupResponse response = signupService.signup(privateId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "액세스 토큰 재발급", description = "HttpOnly Cookie의 refreshToken으로 accessToken을 재발급합니다. refreshToken도 rotate되어 Cookie가 갱신됩니다.", security = {})
    @PostMapping("/oauth/login/reissue")
    public ResponseEntity<LoginPostResponse> accessTokenReissue(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    String refreshToken = cookie.getValue();
                    try {
                        String privateId = jwtProvider.getPrivateIdForRefreshToken(refreshToken);

                        cookieService.deleteOldCookieAndMakeNewCookie(privateId, response, cookie);

                        return ResponseEntity.ok(LoginPostResponse.builder()
                                .accessToken(jwtProvider.createAccessToken(privateId))
                                .build());

                    } catch (ExpiredJwtException e) {
                        cookieService.deleteRefreshTokenCookie(response);
                        log.error("토큰 만료 privateId: {}", jwtProvider.tryExtractUserId(request));
                        throw new NotValidRefreshTokenException();
                    } catch (JwtException e) {
                        cookieService.deleteRefreshTokenCookie(response);
                        log.error("토큰 파싱 에러 privateId: {}", jwtProvider.tryExtractUserId(request));
                        throw new NotValidRefreshTokenException();
                    }
                }
            }
        }
        log.error("Refresh 토큰이 존재하지 않습니다.");
        throw new NotValidRefreshTokenException();
    }

    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴 화면에서 선택한 탈퇴 사유 목록을 저장하고 계정을 탈퇴 처리합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/withdrawal")
    public ResponseEntity<String> withDrawal(@RequestBody WithdrawalRequest request, HttpServletRequest httpServletRequest){
        String privateId = jwtProvider.extractUserId(httpServletRequest);
        userService.withDrawal(request, privateId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("");
    }

}
