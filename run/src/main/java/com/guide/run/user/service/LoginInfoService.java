package com.guide.run.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.guide.run.global.exception.user.authorize.NotValidTmpTokenException;
import com.guide.run.global.exception.user.dto.InvalidAuthNumException;
import com.guide.run.global.exception.user.dto.NotExistAccountIdException;
import com.guide.run.global.exception.user.dto.NotExistPhoneNumException;
import com.guide.run.global.exception.user.logic.InvalidAccountIdAndPhoneException;
import com.guide.run.global.exception.user.resource.NotExistUserException;
import com.guide.run.global.jwt.JwtProvider;
import com.guide.run.global.redis.AuthNumber;
import com.guide.run.global.redis.AuthNumberRepository;
import com.guide.run.global.redis.TmpToken;
import com.guide.run.global.redis.TmpTokenRepository;
import com.guide.run.global.sms.cool.CoolSmsService;
import com.guide.run.user.dto.request.AccountIdPhoneRequest;
import com.guide.run.user.dto.response.FindAccountIdDto;
import com.guide.run.user.dto.response.SmsVerificationExtendResponse;
import com.guide.run.user.dto.response.SmsVerificationIssueResponse;
import com.guide.run.user.dto.response.TokenResponse;
import com.guide.run.user.entity.SignUpInfo;
import com.guide.run.user.entity.user.User;
import com.guide.run.user.repository.SignUpInfoRepository;
import com.guide.run.user.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoginInfoService {
    private final UserRepository userRepository;
    private final SignUpInfoRepository signUpInfoRepository;
    private final AuthNumberRepository authNumberRepository;
    private final TmpTokenRepository tmpTokenRepository;
    private final UserService userService;

    private final CoolSmsService smsService;

    private final JwtProvider jwtProvider;

    private final PasswordEncoder passwordEncoder;

    private static final int AUTH_EXPIRES_IN_SECONDS = 600;
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

    @Transactional
    public SmsVerificationIssueResponse getNumberForAccountId(String phoneNum) throws UnsupportedEncodingException, URISyntaxException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
        String phone = userService.extractNumber(phoneNum);
        userRepository.findUserByPhoneNumber(phone).orElseThrow(NotExistPhoneNumException::new);

        String authNum = createSmsKey();
        String verificationId = UUID.randomUUID().toString();
        smsService.sendSMS(phone, authNum);

        AuthNumber authNumber = new AuthNumber(phone, authNum, "accountId", verificationId);
        authNumberRepository.save(authNumber);

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        ZonedDateTime expiresAt = now.plusSeconds(AUTH_EXPIRES_IN_SECONDS);

        return SmsVerificationIssueResponse.builder()
                .verificationId(verificationId)
                .purpose("ACCOUNT_ID")
                .expiresInSeconds(AUTH_EXPIRES_IN_SECONDS)
                .expiresAt(expiresAt.format(ISO_FORMATTER))
                .serverTime(now.format(ISO_FORMATTER))
                .canExtend(true)
                .build();
    }

    @Transactional
    public SmsVerificationIssueResponse getNumberForPassword(AccountIdPhoneRequest request) throws UnsupportedEncodingException, URISyntaxException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
        String phone = userService.extractNumber(request.getPhoneNum());

        User user1 = userRepository.findUserByPhoneNumber(phone).orElseThrow(NotExistPhoneNumException::new);

        SignUpInfo user2 = signUpInfoRepository.findByAccountId(request.getAccountId()).orElseThrow(NotExistAccountIdException::new);

        if (!user1.getPrivateId().equals(user2.getPrivateId())) {
            throw new InvalidAccountIdAndPhoneException();
        }

        String authNum = createSmsKey();
        String verificationId = UUID.randomUUID().toString();
        smsService.sendSMS(phone, authNum);

        AuthNumber authNumber = new AuthNumber(phone, authNum, "password", verificationId);
        authNumberRepository.save(authNumber);

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        ZonedDateTime expiresAt = now.plusSeconds(AUTH_EXPIRES_IN_SECONDS);

        return SmsVerificationIssueResponse.builder()
                .verificationId(verificationId)
                .purpose("PASSWORD")
                .expiresInSeconds(AUTH_EXPIRES_IN_SECONDS)
                .expiresAt(expiresAt.format(ISO_FORMATTER))
                .serverTime(now.format(ISO_FORMATTER))
                .canExtend(true)
                .build();
    }

    @Transactional
    public SmsVerificationExtendResponse extendVerification(String verificationId) {
        AuthNumber authNumber = authNumberRepository.findByVerificationId(verificationId)
                .orElseThrow(InvalidAuthNumException::new);

        AuthNumber extended = authNumber.withCanExtendFalse();
        authNumberRepository.save(extended);

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        ZonedDateTime expiresAt = now.plusSeconds(AUTH_EXPIRES_IN_SECONDS);

        return SmsVerificationExtendResponse.builder()
                .verificationId(verificationId)
                .expiresInSeconds(AUTH_EXPIRES_IN_SECONDS)
                .expiresAt(expiresAt.format(ISO_FORMATTER))
                .serverTime(now.format(ISO_FORMATTER))
                .canExtend(false)
                .build();
    }

    @Transactional
    public TokenResponse getToken(String authNum) {
        AuthNumber authNumber = authNumberRepository.findByAuthNum(authNum).orElseThrow(InvalidAuthNumException::new);
        User user = userRepository.findUserByPhoneNumber(authNumber.getPhone()).orElseThrow(InvalidAuthNumException::new);
        String purpose = authNumber.getType().equals("accountId") ? "ACCOUNT_ID" : "PASSWORD";
        return TokenResponse.builder()
                .token(jwtProvider.createTmpToken(authNumber.getPhone(), user.getPrivateId(), authNumber.getType()))
                .purpose(purpose)
                .build();
    }


    @Transactional
    public FindAccountIdDto findAccountId(String token){
        TmpToken tmpToken = tmpTokenRepository.findById(token).orElseThrow(NotValidTmpTokenException::new);
        if(tmpToken.getType().equals("accountId")){
            User user = userRepository.findUserByPrivateId(tmpToken.getPrivateId()).orElseThrow(NotExistUserException::new);
            SignUpInfo info = signUpInfoRepository.findById(tmpToken.getPrivateId()).orElseThrow(NotExistUserException::new);

            //가입 아이디 찾고 return
            return FindAccountIdDto.builder()
                    .accountId(info.getAccountId())
                    .createdAt(user.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                    .build();
        }else{
            throw new NotValidTmpTokenException();
        }
    }

    @Transactional
    public void createNewPassword(String token, String password){
        TmpToken tmpToken = tmpTokenRepository.findById(token).orElseThrow(NotValidTmpTokenException::new);
        if(tmpToken.getType().equals("password")){
            userRepository.findUserByPrivateId(tmpToken.getPrivateId()).orElseThrow(NotExistUserException::new);
            //비밀번호 재설정 해줌.
            SignUpInfo info = signUpInfoRepository.findById(tmpToken.getPrivateId()).orElseThrow(NotExistUserException::new);

            SignUpInfo newInfo = SignUpInfo.builder()
                    .privateId(info.getPrivateId())
                    .accountId(info.getAccountId())
                    .password(password)
                    .build();
            newInfo.hashPassword(passwordEncoder); //암호화

            signUpInfoRepository.save(newInfo);//저장
            
        }else{
            throw new NotValidTmpTokenException();
        }
    }

    // 5자리 수 조합 인증코드 만들기
    public static String createSmsKey() {
        StringBuffer key = new StringBuffer();
        Random rnd = new Random();

        for (int i = 0; i < 5; i++) { // 인증코드 5자리
            key.append((rnd.nextInt(10)));
        }
        return key.toString();
    }


}
