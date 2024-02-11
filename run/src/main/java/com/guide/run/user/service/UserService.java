package com.guide.run.user.service;

import com.guide.run.global.exception.user.dto.InvalidItemErrorException;
import com.guide.run.global.jwt.JwtProvider;
import com.guide.run.user.dto.GuideSignupDto;
import com.guide.run.user.dto.ViSignupDto;
import com.guide.run.user.dto.response.SignupResponse;
import com.guide.run.user.entity.*;
import com.guide.run.user.entity.type.Role;
import com.guide.run.user.entity.type.UserType;
import com.guide.run.user.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@RequiredArgsConstructor
@Service
@Slf4j
public class UserService {
    private final ViRepository viRepository;
    private final GuideRepository guideRepository;
    private final UserRepository userRepository;
    private final ArchiveDataRepository archiveDataRepository;
    private final PermissionRepository permissionRepository;
    private final JwtProvider jwtProvider;

    public String getUserStatus(String privateId){
        String reAssignSocialId = reAssignSocialId(privateId);
        User user = userRepository.findById(privateId).orElse(null);
        if(user != null){
                return user.getRole().getValue();
        }else{
            //신규 가입자의 경우 인증을 위해 임시 유저 생성
            //가입이 완료되면 새 토큰 다시 줘야함
            userRepository.save(User.builder()
                    .privateId(privateId)
                    .role(Role.NEW)
                    .userId(getUUID())
                    .build());
            return Role.NEW.getValue();
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
            return "Error";
        }
    }

    public String reAssignReturn(String privateId){
        return reAssignSocialId(privateId);
    }

    //임시 자격 부여
    public void temporaryUserCreate(String socialId){
        User user = User.builder()
                .privateId(socialId)
                .role(Role.NEW)
                .build();
        userRepository.save(user);
    }

}
