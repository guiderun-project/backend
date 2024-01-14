package com.guide.run.user.service;

import com.guide.run.global.jwt.JwtProvider;
import com.guide.run.user.dto.GuideSignupDto;
import com.guide.run.user.dto.ViSignupDto;
import com.guide.run.user.dto.response.SignupResponse;
import com.guide.run.user.entity.Guide;
import com.guide.run.user.entity.type.Role;
import com.guide.run.user.entity.User;
import com.guide.run.user.entity.type.UserStatus;
import com.guide.run.user.entity.Vi;
import com.guide.run.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@RequiredArgsConstructor
@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    public String getUserStatus(String userId){
        String reAssignSocialId = reAssignSocialId(userId);
        User user = userRepository.findById(userId).orElse(null);
        if(user != null){
                return user.getRole().getValue();
        }else{
            //신규 가입자의 경우 인증을 위해 임시 유저 생성
            //가입이 완료되면 새 토큰 다시 줘야함
            userRepository.save(User.builder()
                    .userId(userId)
                    .role(Role.NEW)
                    .uuid(getUUID())
                    .build());
            return Role.NEW.getValue();
        }
    }

    @Transactional
    public SignupResponse viSignup(String userId, ViSignupDto viSignupDto){
        User user = userRepository.findById(reAssignSocialId(userId)).orElse(null);
        if(user!=null) {
            log.info("에러발생");
            return null; //기가입자나 이미 정보를 입력한 회원이 재요청한 경우 이므로 에러 코드 추가
        } else {
            Vi vi = Vi.builder()
                    .runningExp(viSignupDto.isRunningExp())
                    .guideName(viSignupDto.getGuideName())
                    .uuid(getUUID())
                    .userId(userId)
                    .name(viSignupDto.getName())
                    .gender(viSignupDto.getGender())
                    .phoneNumber(viSignupDto.getPhoneNumber())
                    .openNumber(viSignupDto.isOpenNumber())
                    .age(viSignupDto.getAge())
                    .detailRecord(viSignupDto.getDetailRecord())
                    .recordDegree(viSignupDto.getRecordDegree())
                    .role(Role.VWAIT)
                    .snsId(viSignupDto.getSnsId())
                    .openSNS(viSignupDto.isOpenSNS())
                    .build();

            userRepository.delete(userRepository.findById(userId).orElse(null)); //임시 유저 삭제

            Vi newVi = userRepository.save(vi);

            SignupResponse response = SignupResponse
                    .builder()
                    .accessToken(jwtProvider.createAccessToken(userId))
                    .uuid(newVi.getUuid())
                    .userStatus(newVi.getRole().getValue())
                    .build();

            return response;
        }
    }

    @Transactional
    public SignupResponse guideSignup(String userId, GuideSignupDto guideSignupDto){
        User user = userRepository.findById(reAssignSocialId(userId)).orElse(null);
        if(user!=null) {
            log.info("에러발생");
            return null;
        } else {
            Guide guide = Guide.builder()
                    .uuid(getUUID())
                    .userId(userId)
                    .name(guideSignupDto.getName())
                    .gender(guideSignupDto.getGender())
                    .phoneNumber(guideSignupDto.getPhoneNumber())
                    .openNumber(guideSignupDto.isOpenNumber())
                    .age(guideSignupDto.getAge())
                    .detailRecord(guideSignupDto.getDetailRecord())
                    .recordDegree(guideSignupDto.getRecordDegree())
                    .snsId(guideSignupDto.getSnsId())
                    .openSNS(guideSignupDto.isOpenSNS())
                    .guideExp(guideSignupDto.isGuideExp())
                    .viName(guideSignupDto.getViName())
                    .viCount(guideSignupDto.getViCount())
                    .viRecord(guideSignupDto.getViRecord())
                    .role(Role.GWAIT)
                    .build();

            userRepository.delete(userRepository.findById(userId).orElse(null)); //임시 유저 삭제

            Guide newGuide = userRepository.save(guide);

            SignupResponse response = SignupResponse.builder()
                    .uuid(newGuide.getUuid())
                    .accessToken(jwtProvider.createAccessToken(userId))
                    .userStatus(newGuide.getRole().getValue())
                    .build();

            return response;
        }
    }



    public String getUUID(){
        String id = UUID.randomUUID().toString();
        return id;
    }


    //socialId = userId 재할당
    //기가입자 및 이미 정보를 입력한 회원은 재할당 하지 않음
    private String reAssignSocialId(String userId) {
        if(userId.startsWith("kakao_")){
            return userId;
        }else if(userId.startsWith("kakao")){
            return "kakao_"+userId.substring(6);
        }
        else{
            return "Error";
        }
    }

    //임시 자격 부여
    public void temporaryUserCreate(String socialId){
        User user = User.builder()
                .userId(socialId)
                .role(Role.NEW)
                .build();
        userRepository.save(user);
    }

}
