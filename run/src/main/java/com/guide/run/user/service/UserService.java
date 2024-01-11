package com.guide.run.user.service;

import com.guide.run.user.dto.UserSignupDto;
import com.guide.run.user.entity.Role;
import com.guide.run.user.entity.User;
import com.guide.run.user.entity.UserStatus;
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
            if(user.getRole().equals(Role.WAIT)){
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
    public User viSignup(String socialId, UserSignupDto userSignupDto){
        System.out.println("Vi = " + userSignupDto.getSnsId());
        Vi vi = Vi.builder()
                .runningExp(userSignupDto.isRunningExp())
                .guideName(userSignupDto.getGuideName())
                .socialId(socialId)
                .name(userSignupDto.getName())
                .gender(userSignupDto.getGender())
                .phoneNumber(userSignupDto.getPhoneNumber())
                .age(userSignupDto.getAge())
                .detailRecord(userSignupDto.getDetailRecord())
                .recordDegree(userSignupDto.getRecordDegree())
                .role(Role.WAIT)
                .snsId(userSignupDto.getSnsId())
                .build();

        return  userRepository.save(vi);
    }

}
