package com.guide.run.user.service;

import com.guide.run.user.entity.SignUpInfo;
import com.guide.run.user.entity.user.User;
import com.guide.run.user.entity.type.Role;
import com.guide.run.user.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;


@RequiredArgsConstructor
@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final SignUpInfoRepository signUpInfoRepository;

    public boolean getUserStatus(String privateId){
        User user = userRepository.findById(privateId).orElse(null);
        if(user != null){
            if(user.getPhoneNumber()==null) {
                return false;
            }
            else
                return true;
        }else{
                //신규 가입자의 경우 인증을 위해 임시 유저 생성
                //가입이 완료되면 새 토큰 다시 줘야함
                userRepository.save(User.builder()
                        .privateId(privateId)
                        .role(Role.NEW)
                        .userId(getUUID())
                        .build());
                return false;
        }
    }

    public String getUUID(){
        String id = UUID.randomUUID().toString();
        return id;
    }


    //userId = privateId로 변경, uuid -> userId 로 변경
    //기가입자 및 이미 정보를 입력한 회원은 재할당 하지 않음
    private String reAssignSocialId(String privateId) {
        if(privateId.startsWith("kakao_")){
            return privateId;
        }else if(privateId.startsWith("kakao")){
            return "kakao_"+privateId.substring(6);
        }
        else{
            return "Error"; //todo : 이 부분 에러코드 추가해야 합니다.
        }
    }

    public String reAssignReturn(String privateId){
        return reAssignSocialId(privateId);
    }


    public String extractNumber(String phoneNum){
        return phoneNum.replaceAll("[^0-9]", "");
    }

    public boolean isAccountIdExist(String accountId) {
        Optional<SignUpInfo> byAccountId = signUpInfoRepository.findByAccountId(accountId);
        return !byAccountId.isEmpty();
    }

}
