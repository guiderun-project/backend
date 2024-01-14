package com.guide.run.user.service;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public String getUserStatus(String socialId){
        User user = userRepository.findById(socialId).orElse(null);
        if(user != null){
            if(user.getRole().equals(Role.VWAIT)){
                return UserStatus.WAIT.getValue();
            } else{
                return UserStatus.EXIST.getValue();
            }
        }
        else{
            return UserStatus.NEW.getValue();
        }
    }

    @Transactional
    public SignupResponse viSignup(String userId, ViSignupDto viSignupDto){
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

        Vi newVi = userRepository.save(vi);

        SignupResponse response = SignupResponse
                .builder()
                .uuid(newVi.getUuid())
                .role(newVi.getRole().getValue())
                .build();

        return response;
    }

    @Transactional
    public SignupResponse guideSignup(String userId, GuideSignupDto guideSignupDto){
        Guide guide = Guide.builder()
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

        Guide newGuide = userRepository.save(guide);

        SignupResponse response = SignupResponse.builder()
                .uuid(newGuide.getUuid())
                .role(newGuide.getRole().getValue())
                .build();

        return  response;
    }

    public String getUUID(){
        String id = UUID.randomUUID().toString();
        return id;
    }

}
