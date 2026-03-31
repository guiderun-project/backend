package com.guide.run.user.controller;

import com.guide.run.global.cookie.service.CookieService;
import com.guide.run.global.exception.auth.authorize.NotValidRefreshTokenException;
import com.guide.run.global.exception.user.dto.DuplicatedUserIdException;
import com.guide.run.global.jwt.JwtProvider;
import com.guide.run.user.dto.GuideSignupDto;
import com.guide.run.user.dto.ReissuedAccessTokenDto;
import com.guide.run.user.dto.ViSignupDto;
import com.guide.run.user.dto.request.AccountIdDto;
import com.guide.run.user.dto.request.GeneralLoginRequest;
import com.guide.run.user.dto.request.WithdrawalRequest;
import com.guide.run.user.dto.response.IsDuplicatedResponse;
import com.guide.run.user.dto.response.LoginResponse;
import com.guide.run.user.dto.response.SignupResponse;
import com.guide.run.user.profile.OAuthProfile;
import com.guide.run.user.service.GuideService;
import com.guide.run.user.service.ProviderService;
import com.guide.run.user.service.UserService;
import com.guide.run.user.service.ViService;
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
@CrossOrigin(origins = {"https://dev.guiderun.org", "https://guiderun.org","https://www.guiderun.org", "http://localhost:3000", "http://localhost:8080"},
maxAge = 3600,
allowCredentials = "true")
@Tag(name = "Auth", description = "로그인, 회원가입, 토큰 재발급, 중복 확인과 회원 탈퇴를 다루는 인증 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SignController {
    private final ProviderService providerService;
    private final JwtProvider jwtProvider;
    private final CookieService cookieService;
    private final UserService userService;
    private final ViService viService;
    private final GuideService guideService;


    @Operation(summary = "일반 로그인", description = "로그인 화면에서 계정 ID와 비밀번호로 로그인합니다. 응답 본문에는 액세스 토큰을, 브라우저 쿠키에는 refreshToken을 내려줍니다.", security = {})
    @PostMapping("/login")
    public LoginResponse generalLogin(@RequestBody GeneralLoginRequest request,HttpServletRequest httpServletRequest,
                                      HttpServletResponse httpServletResponse){

        String privateId = userService.generalLogin(request.getAccountId(), request.getPassword());
        boolean isExist = userService.getUserStatus(privateId);

        boolean isExistCookie =false;


        if(httpServletRequest.getCookies() !=null){
            for(Cookie cookie: httpServletRequest.getCookies()){
                if(cookie.getName().equals("refreshToken")){
                    //기존 쿠키 만료 처리 및 새 쿠키 생성
                    cookieService.deleteOldCookieAndMakeNewCookie(privateId,httpServletResponse, cookie);
                    isExistCookie=true;
                }
            }
        }
        if(!isExistCookie) {
            cookieService.createCookie("refreshToken", httpServletResponse, privateId);
        }

        return LoginResponse.builder()
                .accessToken(jwtProvider.createAccessToken(privateId))
                .refreshToken(jwtProvider.createRefreshToken(privateId))
                .isExist(isExist)
                .build();
    }

    @Operation(summary = "카카오 OAuth 로그인", description = "프론트 OAuth 콜백 화면에서 받은 카카오 인가 코드를 전달받아 로그인합니다. 신규 사용자는 `isExist=false`로 내려와 추가 회원가입 흐름으로 이동합니다.", security = {})
    @PostMapping("/oauth/login/kakao")
    public LoginResponse kakaoLogin(@RequestParam("code") String code, HttpServletRequest request,HttpServletResponse response) throws CommunicationException {
        String accessToken = providerService.getAccessToken(code, "kakao").getAccess_token();
        OAuthProfile oAuthProfile = providerService.getProfile(accessToken,"kakao");
        String privateId = oAuthProfile.getSocialId();
        boolean isExist = userService.getUserStatus(privateId);

        boolean isExistCookie =false;


        if(request.getCookies() !=null){
            for(Cookie cookie: request.getCookies()){
                if(cookie.getName().equals("refreshToken")){
                    cookieService.deleteOldCookieAndMakeNewCookie(privateId,response, cookie);
                    isExistCookie=true;
                }
            }
        }
        if(!isExistCookie) {
            cookieService.createCookie("refreshToken", response, privateId);
        }


        return LoginResponse.builder()
                .accessToken(jwtProvider.createAccessToken(privateId))
                .refreshToken(jwtProvider.createRefreshToken(privateId))
                .isExist(isExist)
                .build();
    }

    @Operation(summary = "VI 회원가입 완료", description = "소셜 로그인 후 NEW 권한 사용자가 VI 회원가입 폼을 제출할 때 호출됩니다. 프론트의 회원가입 화면에서 입력한 기본 정보, 러닝 정보, 약관 동의 정보를 함께 받습니다.", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/signup/vi")
    public ResponseEntity<SignupResponse> viSignup(@RequestBody @Valid ViSignupDto viSignupDto, HttpServletRequest httpServletRequest){
        String privateId = jwtProvider.extractUserId(httpServletRequest);
        if(userService.isAccountIdExist(viSignupDto.getAccountId())){
            throw new DuplicatedUserIdException();
        }else{
            SignupResponse response = viService.viSignup(privateId, viSignupDto);
            userService.signUpATA(privateId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
    }


    @Operation(summary = "Guide 회원가입 완료", description = "소셜 로그인 후 NEW 권한 사용자가 Guide 회원가입 폼을 제출할 때 호출됩니다. 프론트의 회원가입 화면에서 입력한 기본 정보와 가이드 경험 정보를 함께 받습니다.", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/signup/guide")
    public ResponseEntity<SignupResponse> guideSignup(@RequestBody @Valid GuideSignupDto guideSignupDto, HttpServletRequest httpServletRequest){
        String privateId = jwtProvider.extractUserId(httpServletRequest);
        if(userService.isAccountIdExist(guideSignupDto.getAccountId())){
            throw new DuplicatedUserIdException();
        }else{
            SignupResponse response = guideService.guideSignup(privateId, guideSignupDto);
            userService.signUpATA(privateId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
    }


    @Operation(summary = "구글 OAuth 토큰 교환", description = "구글 인가 코드를 액세스 토큰으로 교환합니다. 현재 프론트 실사용 흐름보다는 보조 인증 경로에 가깝습니다.", security = {})
    @PostMapping("/oauth/token/google")
    public LoginResponse googleSignup(@RequestParam("code") String code,HttpServletResponse response) throws CommunicationException {
        String accessToken = providerService.getAccessToken(code, "google").getAccess_token();
        OAuthProfile oAuthProfile = providerService.getProfile(accessToken,"google");
        String userId = oAuthProfile.getSocialId();

        return LoginResponse.builder()
                .accessToken(jwtProvider.createAccessToken(userId))
                .build();
    }
    @Operation(summary = "액세스 토큰 재발급", description = "브라우저 쿠키의 refreshToken으로 액세스 토큰을 재발급합니다. 프론트 앱 초기 진입과 401 재시도 처리에서 사용됩니다.", security = {})
    @GetMapping("/oauth/login/reissue")
    public ReissuedAccessTokenDto accessTokenReissue(HttpServletRequest request, HttpServletResponse response){
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    String refreshToken = cookie.getValue();
                    try {

                        // refresh 토큰의 유효성 및 만료 여부 체크 (만료된 경우 예외 발생)
                        String privateId = jwtProvider.getPrivateIdForRefreshToken(refreshToken);
                        boolean isExist = userService.getUserStatus(privateId);

                        //기존 쿠키 만료 처리 및 새 쿠키 생성
                        cookieService.deleteOldCookieAndMakeNewCookie(privateId,response, cookie);

                        // 유효한 토큰인 경우 엑세스 토큰 재발급
                        return ReissuedAccessTokenDto.builder()
                                .accessToken(jwtProvider.createAccessToken(privateId))
                                .isExist(isExist)
                                .build();

                    } catch (ExpiredJwtException e) {
                        // refresh 토큰이 만료된 경우
                        cookieService.deleteRefreshTokenCookie(response);
                        log.error("토큰 만료 privateId: {}", jwtProvider.extractUserId(request));
                        throw new NotValidRefreshTokenException();
                    } catch (JwtException e) {
                        // 토큰 파싱 에러
                        cookieService.deleteRefreshTokenCookie(response);
                        log.error("토큰 파싱 에러 privateId: {}", jwtProvider.extractUserId(request));
                        throw new NotValidRefreshTokenException();
                    }
                }
            }
        }
        log.error("Refresh 토큰이 존재하지 않습니다. privateId: {}", jwtProvider.extractUserId(request));
        throw new NotValidRefreshTokenException();
    }

    //아이디 중복확인
    @Operation(summary = "회원가입용 계정 ID 중복 확인", description = "회원가입 화면에서 입력한 accountId가 사용 가능한지 확인합니다. 소셜 로그인 이후 NEW 권한 상태에서 호출됩니다.", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/signup/duplicated")
    public ResponseEntity<IsDuplicatedResponse> isIdDuplicated(@RequestBody AccountIdDto aa){
        IsDuplicatedResponse response =
                IsDuplicatedResponse.builder()
                        .isUnique(!userService.isAccountIdExist(aa.getAccountId()))
                        .build();
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴 화면에서 선택한 탈퇴 사유 목록을 저장하고 계정을 탈퇴 처리합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/withdrawal")
    public ResponseEntity<String> withDrawal(@RequestBody WithdrawalRequest request, HttpServletRequest httpServletRequest){
        String privateId = jwtProvider.extractUserId(httpServletRequest);
        userService.withDrawal(request, privateId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("");
    }

    @Operation(summary = "네이버 OAuth 로그인", description = "네이버 인가 코드를 전달받아 로그인합니다. 현재 프론트의 주 인증 경로는 아니지만 운영 가능한 공개 인증 API입니다.", security = {})
    @PostMapping("/oauth/login/naver")
    public LoginResponse naverLogin(@RequestParam("code") String code, HttpServletRequest request,HttpServletResponse response) throws CommunicationException {
        String accessToken = providerService.getAccessToken(code, "naver").getAccess_token();
        OAuthProfile oAuthProfile = providerService.getProfile(accessToken,"naver");
        String privateId = oAuthProfile.getSocialId();
        boolean isExist = userService.getUserStatus(privateId);


        return LoginResponse.builder()
                .accessToken(jwtProvider.createAccessToken(privateId))
                .refreshToken(jwtProvider.createRefreshToken(privateId))
                .isExist(isExist)
                .build();
    }

}
