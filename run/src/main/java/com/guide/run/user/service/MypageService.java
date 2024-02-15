package com.guide.run.user.service;

import com.guide.run.global.exception.user.resource.NotExistUserException;
import com.guide.run.user.dto.GlobalUserInfoDto;
import com.guide.run.user.entity.user.User;
import com.guide.run.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MypageService {
    private final UserRepository userRepository;

    public GlobalUserInfoDto getGlobalUserInfo(String privateId){
        User user = userRepository.findById(privateId).orElseThrow(
                NotExistUserException::new
        );

        return GlobalUserInfoDto.userToInfoDto(user);
    }
}
