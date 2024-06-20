package com.guide.run.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.guide.run.global.exception.user.authorize.NotValidTmpTokenException;
import com.guide.run.global.exception.user.dto.InvalidAuthNumException;
import com.guide.run.global.exception.user.dto.NotExistAccountIdException;
import com.guide.run.global.exception.user.dto.NotExistPhoneNumException;
import com.guide.run.global.exception.user.logic.InvalidAccountIdAndPhoneException;
import com.guide.run.global.exception.user.resource.NotExistUserException;
import com.guide.run.global.jwt.JwtProvider;
import com.guide.run.global.naverCloud.SmsService;
import com.guide.run.global.naverCloud.dto.MessageDto;
import com.guide.run.global.naverCloud.dto.SmsResponse;
import com.guide.run.global.redis.AuthNumber;
import com.guide.run.global.redis.AuthNumberRepository;
import com.guide.run.global.redis.TmpToken;
import com.guide.run.global.redis.TmpTokenRepository;
import com.guide.run.user.dto.request.AccountIdPhoneRequest;
import com.guide.run.user.dto.response.FindAccountIdDto;
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
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoginInfoService {
    private final UserRepository userRepository;
    private final SignUpInfoRepository signUpInfoRepository;
    private final AuthNumberRepository authNumberRepository;
    private final TmpTokenRepository tmpTokenRepository;
    private final UserService userService;

    private final SmsService smsService;

    private final JwtProvider jwtProvider;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void getNumberForAccountId(String phoneNum) throws UnsupportedEncodingException, URISyntaxException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
        String phone = userService.extractNumber(phoneNum);
        User user = userRepository.findUserByPhoneNumber(phone).orElseThrow(NotExistPhoneNumException::new);

        //todo : 네이버 클라우드 인증 완료 후 테스트 진행
        //인증번호 전송 api 실행
       // SmsResponse smsResponse = smsService.sendSms(
         //       MessageDto.builder()
           //             .to(phoneNum)
             //           .build()
        //);

        //인증번호 저장
        //AuthNumber authNumber = new AuthNumber(phone, smsResponse.getAuthNum(), "accountId");
        AuthNumber authNumber = new AuthNumber(phone, "12345", "accountId");
        authNumberRepository.save(authNumber);

    }

    @Transactional
    public void getNumberForPassword(AccountIdPhoneRequest request) throws UnsupportedEncodingException, URISyntaxException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
        String phone = userService.extractNumber(request.getPhoneNum());

        User user1 = userRepository.findUserByPhoneNumber(phone).orElseThrow(NotExistPhoneNumException::new);

        SignUpInfo user2 = signUpInfoRepository.findByAccountId(request.getAccountId()).orElseThrow(NotExistAccountIdException::new);

        //번호와 아이디 정보가 일치하는지 확인해야 함.
        if(!user1.getPrivateId().equals(user2.getPrivateId())){
            throw new InvalidAccountIdAndPhoneException();
        }

        //인증번호 전송 api 실행
       // SmsResponse smsResponse = smsService.sendSms(
          //      MessageDto.builder()
            //            .to(request.getPhoneNum())
              //          .build()
        //);

        //인증번호 저장
        //AuthNumber authNumber = new AuthNumber(phone, smsResponse.getAuthNum(), "password");
        AuthNumber authNumber = new AuthNumber(phone, "12345", "password");
        authNumberRepository.save(authNumber);

    }

    @Transactional
    public TokenResponse getToken(String authNum) {
        AuthNumber authNumber = authNumberRepository.findByAuthNum(authNum).orElse(null);//인증번호가 일치하지 않음 에러

        if(authNumber==null){
            throw new InvalidAuthNumException();
        }
        User user = userRepository.findUserByPhoneNumber(authNumber.getPhone()).orElseThrow(InvalidAuthNumException::new);
        return TokenResponse.builder()
                .token(jwtProvider.createTmpToken(authNumber.getPhone(), user.getPrivateId(), authNumber.getType()))
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
            throw new RuntimeException("다른 인증 토큰 에러");
        }
    }

    @Transactional
    public void createNewPassword(String token, String password){
        TmpToken tmpToken = tmpTokenRepository.findById(token).orElseThrow(NotValidTmpTokenException::new);
        if(tmpToken.getType().equals("password")){
            User user = userRepository.findUserByPrivateId(tmpToken.getPrivateId()).orElseThrow(NotExistUserException::new);
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


}
