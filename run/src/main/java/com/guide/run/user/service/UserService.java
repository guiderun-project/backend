package com.guide.run.user.service;

import com.guide.run.user.dto.ViSignupDto;
import com.guide.run.user.entity.Role;
import com.guide.run.user.entity.User;
import com.guide.run.user.entity.Vi;
import com.guide.run.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    //WAIT 정보를 입력까지 마치고 가입 승인전인 사용자
    //NEW 소셜 인증만 한 새로운 사용자
    //EXIST 기가입자
    public String getUserStatus(String socialId){
        String reAssignSocialId = reAssignSocialId(socialId);
        User user = userRepository.findBySocialId(reAssignSocialId).orElse(null);
        if(user != null){
            return user.getRole().getValue();
        }
        else{
            //신규 가입자의 경우 인증을 위해 임시 유저 생성
            //가입이 완료되면 새 토큰 다시 줘야함
            userRepository.save(User.builder()
                    .socialId(socialId)
                    .role(Role.NEW)
                    .build());
            return Role.NEW.getValue();
        }
    }

    @Transactional
    public User viSignup(String socialId, ViSignupDto viSignupDto){
        System.out.println("======"+socialId);
        User user = userRepository.findBySocialId(reAssignSocialId(socialId)).orElse(null);
        System.out.println("user = " + user);
        if(user!=null) {
            log.info("에러발생");
            return null; //기가입자나 이미 정보를 입력한 회원이 재요청한 경우 이므로 에러 코드 추가
        }else{
            User vi = Vi.builder()
                    .runningExp(viSignupDto.isRunningExp())
                    .guideName(viSignupDto.getGuideName())
                    .socialId(reAssignSocialId(socialId))
                    .name(viSignupDto.getName())
                    .gender(viSignupDto.getGender())
                    .phoneNumber(viSignupDto.getPhoneNumber())
                    .age(viSignupDto.getAge())
                    .detailRecord(viSignupDto.getDetailRecord())
                    .recordDegree(viSignupDto.getRecordDegree())
                    .role(Role.VWAIT)
                    .snsId(viSignupDto.getSnsId())
                    .build();
            userRepository.delete(userRepository.findBySocialId(socialId).orElse(null)); //임시 유저 삭제
            return  userRepository.save(vi);
        }
    }

    //socialId 재할당
    public String reAssignSocialId(String socialId){
        if(socialId.startsWith("kakao")){
            return "kakao_"+socialId.substring(6);
        }else{
            return "error";
        }
    }

    //임시 자격 부여
    public void temporaryUserCreate(String socialId){
        User user = User.builder()
                .socialId(socialId)
                .role(Role.NEW)
                .build();
        userRepository.save(user);
    }
}
