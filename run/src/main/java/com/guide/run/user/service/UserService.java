package com.guide.run.user.service;

import com.guide.run.user.dto.ViSignupDto;
import com.guide.run.user.entity.type.Role;
import com.guide.run.user.entity.User;
import com.guide.run.user.entity.type.UserStatus;
import com.guide.run.user.entity.Vi;
import com.guide.run.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public String getUserStatus(String socialId){
        User user = userRepository.findBySocialId(socialId).orElse(null);
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
    public User viSignup(String socialId, ViSignupDto viSignupDto){
        Vi vi = Vi.builder()
                .runningExp(viSignupDto.isRunningExp())
                .guideName(viSignupDto.getGuideName())
                .socialId(socialId)
                .name(viSignupDto.getName())
                .gender(viSignupDto.getGender())
                .phoneNumber(viSignupDto.getPhoneNumber())
                .age(viSignupDto.getAge())
                .detailRecord(viSignupDto.getDetailRecord())
                .recordDegree(viSignupDto.getRecordDegree())
                .role(Role.VWAIT)
                .snsId(viSignupDto.getSnsId())
                .build();

        return  userRepository.save(vi);
    }

}
