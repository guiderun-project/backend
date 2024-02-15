package com.guide.run.user.service;

import com.guide.run.global.exception.user.authorize.ExistUserException;
import com.guide.run.global.jwt.JwtProvider;
import com.guide.run.temp.member.service.TmpService;
import com.guide.run.user.dto.GuideSignupDto;
import com.guide.run.user.dto.response.SignupResponse;
import com.guide.run.user.entity.*;
import com.guide.run.user.entity.type.Role;
import com.guide.run.user.entity.type.UserType;
import com.guide.run.user.entity.user.Guide;
import com.guide.run.user.entity.user.User;
import com.guide.run.user.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class GuideService {
    private final GuideRepository guideRepository;
    private final UserRepository userRepository;
    private final ArchiveDataRepository archiveDataRepository;
    private final PermissionRepository permissionRepository;
    private final JwtProvider jwtProvider;
    private final UserService userService;
    private final PasswordEncoder bCryptPasswordEncoder;
    private final SignUpInfoRepository signUpInfoRepository;

    private final TmpService tmpService;

    @Transactional
    public SignupResponse guideSignup(String privateId, GuideSignupDto guideSignupDto){
        User user = userRepository.findById(privateId).orElse(null);
        if(user!=null && !user.getRole().equals(Role.NEW)) {
            //log.info("에러발생");
            throw new ExistUserException();
        } else {
            String phoneNum = userService.extractNumber(guideSignupDto.getPhoneNumber());

            //가입 전 회원정보 연결
            tmpService.updateMember(phoneNum, privateId);

            User guide = User.builder()
                    .userId(userService.getUUID())
                    .privateId(privateId)
                    .name(guideSignupDto.getName())
                    .gender(guideSignupDto.getGender())
                    .phoneNumber(phoneNum)
                    .openNumber(guideSignupDto.isOpenNumber())
                    .age(guideSignupDto.getAge())
                    .detailRecord(guideSignupDto.getDetailRecord())
                    .recordDegree(guideSignupDto.getRecordDegree())
                    .snsId(guideSignupDto.getSnsId())
                    .openSns(guideSignupDto.isOpenSns())
                    .role(Role.WAIT)
                    .type(UserType.GUIDE)
                    .build();

            Guide guideInfo = Guide.builder()
                    .privateId(privateId)
                    .guideExp(guideSignupDto.isGuideExp())
                    .viName(guideSignupDto.getViName())
                    .viCount(guideSignupDto.getViCount())
                    .viRecord(guideSignupDto.getViRecord())
                    .build();


            userRepository.delete(userRepository.findById(privateId).orElse(null)); //임시 유저 삭제
            //todo : 에러코드 추가해야 합니다.

            User newUser = userRepository.save(guide);
            guideRepository.save(guideInfo);


            ArchiveData archiveData = ArchiveData.builder()
                    .privateId(privateId)
                    .howToKnow(guideSignupDto.getHowToKnow())
                    .motive(guideSignupDto.getMotive())
                    .runningPlace(guideSignupDto.getRunningPlace())
                    .build();

            archiveDataRepository.save(archiveData); //안 쓰는 데이터 저장


            Permission permission = Permission.builder()
                    .privateId(privateId)
                    .privacy(guideSignupDto.isPrivacy())
                    .portraitRights(guideSignupDto.isPortraitRights())
                    .build();

            permissionRepository.save(permission); //약관 동의 저장

            SignUpInfo signUpInfo = SignUpInfo.builder()
                    .privateId(privateId)
                    .accountId(guideSignupDto.getAccountId())
                    .password(guideSignupDto.getPassword())
                    .build();

            signUpInfo.hashPassword(bCryptPasswordEncoder); //비밀번호 암호화

            signUpInfoRepository.save(signUpInfo); //가입 정보 저장

            SignupResponse response = SignupResponse.builder()
                    .uuid(newUser.getUserId())
                    .accessToken(jwtProvider.createAccessToken(privateId))
                    .userStatus(newUser.getRole().getValue())
                    .build();

            return response;
        }
    }
}
