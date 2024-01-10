package com.guide.run.user.service;

import com.guide.run.user.entity.Role;
import com.guide.run.user.entity.User;
import com.guide.run.user.entity.UserStatus;
import com.guide.run.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public String getUserStatus(String socialId){
        User user = userRepository.findBySocialId(socialId).orElse(null);
        if(user != null){
            if(user.getRole().equals(Role.User)){
                return UserStatus.WAIT.getValue();
            } else{
                return UserStatus.USER.getValue();
            }
        }
        else{
            return UserStatus.NEW.getValue();
        }
    }
}
