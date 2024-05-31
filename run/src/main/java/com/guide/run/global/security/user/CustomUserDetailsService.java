package com.guide.run.global.security.user;

import com.guide.run.global.exception.user.resource.NotExistUserException;
import com.guide.run.user.entity.user.User;
import com.guide.run.user.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String socialId) throws UsernameNotFoundException{
        User user = userRepository.findUserByPrivateId(socialId).orElseThrow(() -> new NotExistUserException());
        return new CustomUserDetails(user);
    }
}
