package com.guide.run.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import com.guide.run.user.entity.SignUpInfo;
import com.guide.run.user.entity.user.User;
import com.guide.run.user.repository.SignUpInfoRepository;
import com.guide.run.user.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
public class LoginInfoService {
    private final UserRepository userRepository;
    private final SignUpInfoRepository signUpInfoRepository;
    private final AuthNumberRepository authNumberRepository;
    private final TmpTokenRepository tmpTokenRepository;
    private final UserService userService;

    private final SmsService smsService;

    private JwtProvider jwtProvider;

    @Transactional
    public void getNumberForAccountId(String phoneNum) throws UnsupportedEncodingException, URISyntaxException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
        String phone = userService.extractNumber(phoneNum);
        //todo : 존재하지 않는 번호 에러로 변경해야 함.
        User user = userRepository.findUserByPhoneNumber(phone).orElseThrow(NotExistUserException::new);

        //인증번호 전송 api 실행
        SmsResponse smsResponse = smsService.sendSms(
                MessageDto.builder()
                        .to(phoneNum)
                        .build()
        );

        //인증번호 저장
        AuthNumber authNumber = new AuthNumber("accountId"+phone, smsResponse.getAuthNum());
        authNumberRepository.save(authNumber);

    }

    @Transactional
    public void getNumberForPassword(AccountIdPhoneRequest request) throws UnsupportedEncodingException, URISyntaxException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
        String phone = userService.extractNumber(request.getPhoneNum());

        //존재하지 않는 번호 에러로 변경해야 함.
        User user1 = userRepository.findUserByPhoneNumber(phone).orElseThrow(NotExistUserException::new);

        //존재하지 않는 아이디 에러로 변경해야 함.
        SignUpInfo user2 = signUpInfoRepository.findByAccountId(request.getAccountId()).orElseThrow(NotExistUserException::new);

        //번호와 아이디 정보가 일치하는지 확인해야 함.
        if(!user1.getPrivateId().equals(user2.getPrivateId())){
            throw new RuntimeException("정보가 일치하지 않는 에러");
        }

        //인증번호 전송 api 실행
        SmsResponse smsResponse = smsService.sendSms(
                MessageDto.builder()
                        .to(request.getPhoneNum())
                        .build()
        );

        //인증번호 저장
        AuthNumber authNumber = new AuthNumber("password"+phone, smsResponse.getAuthNum());
        authNumberRepository.save(authNumber);

    }

    @Transactional
    public String getToken(String authNum){
        AuthNumber authNumber = authNumberRepository.findByAuthNum(authNum).orElseThrow(
                RuntimeException::new
        );//인증번호가 일치하지 않음 에러

        User user;
        if(authNumber.getPhone().startsWith("p")){
            user = userRepository.findUserByPhoneNumber(authNumber.getPhone().substring(("password").length())).orElseThrow(NotExistUserException::new);
            return jwtProvider.createTmpToken(authNumber.getPhone(),user.getPrivateId());
        }else if(authNumber.getPhone().startsWith("a")){
            user = userRepository.findUserByPhoneNumber(authNumber.getPhone().substring(("accountId").length())).orElseThrow(NotExistUserException::new);
            return jwtProvider.createTmpToken(authNumber.getPhone(),user.getPrivateId());
        }else{
            throw new RuntimeException("인증번호가 일치하지 않음 에러");
        }
    }

    @Transactional
    public void findAccountId(String token){
        //잘못된 토큰 에러로 변경
        TmpToken tmpToken = tmpTokenRepository.findById(token).orElseThrow(RuntimeException::new);
        if(tmpToken.getToken().startsWith("a")){
            User user = userRepository.findUserByPrivateId(tmpToken.getPrivateId()).orElseThrow(NotExistUserException::new);
            //가입 아이디 찾고 return
        }else{
            throw new RuntimeException("다른 인증 토큰 에러");
        }
    }

    @Transactional
    public void createNewPassword(String token){
        //잘못된 토큰 에러로 변경
        TmpToken tmpToken = tmpTokenRepository.findById(token).orElseThrow(RuntimeException::new);
        if(tmpToken.getToken().startsWith("p")){
            User user = userRepository.findUserByPrivateId(tmpToken.getPrivateId()).orElseThrow(NotExistUserException::new);
            //비밀번호 재설정 해줌.
        }else{
            throw new RuntimeException("다른 인증 토큰 에러");
        }
    }


}
