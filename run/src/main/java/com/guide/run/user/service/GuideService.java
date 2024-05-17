package com.guide.run.user.service;

import com.guide.run.global.exception.user.authorize.ExistUserException;
import com.guide.run.global.exception.user.resource.NotExistUserException;
import com.guide.run.global.jwt.JwtProvider;
import com.guide.run.temp.member.dto.CntDTO;
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
    private final JwtProvider jwtProvider;
    private final UserService userService;
    private final PasswordEncoder bCryptPasswordEncoder;
    private final SignUpInfoRepository signUpInfoRepository;

    private final TmpService tmpService;

    @Transactional
    public SignupResponse guideSignup(String privateId, GuideSignupDto guideSignupDto){
        User user = userRepository.findById(privateId).orElse(null);
        if(user!=null && !user.getRole().equals(Role.ROLE_NEW)) {
            //log.info("에러발생");
            throw new ExistUserException();
        } else {
            String phoneNum = userService.extractNumber(guideSignupDto.getPhoneNumber());

            //가입 전 회원정보 연결
            CntDTO cntDTO = tmpService.updateMember(phoneNum, privateId);

            User guide = User.builder()
                    .userId(userService.getUUID())
                    .privateId(privateId)
                    .name(guideSignupDto.getName())
                    .gender(guideSignupDto.getGender())
                    .phoneNumber(phoneNum)
                    .isOpenNumber(guideSignupDto.getIsOpenNumber())
                    .age(guideSignupDto.getAge())
                    .detailRecord(guideSignupDto.getDetailRecord())
                    .recordDegree(guideSignupDto.getRecordDegree())
                    .competitionCnt(cntDTO.getCompetitionCnt())
                    .trainingCnt(cntDTO.getTrainingCnt())
                    .snsId(guideSignupDto.getSnsId())
                    .isOpenSns(guideSignupDto.getIsOpenSns())
                    .role(Role.ROLE_WAIT)
                    .type(UserType.GUIDE)
                    .build();

            Guide guideInfo = Guide.builder()
                    .privateId(privateId)
                    .isGuideExp(guideSignupDto.getIsGuideExp())
                    .viName(guideSignupDto.getViName())
                    .viCount(guideSignupDto.getViCount())
                    .guidingPace(guideSignupDto.getGuidingPace())
                    .viRecord(guideSignupDto.getViRecord())
                    .build();


            userRepository.delete(userRepository.findById(privateId).orElseThrow(
                    NotExistUserException::new
            )); //임시 유저 삭제

            User newUser = userRepository.save(guide);
            guideRepository.save(guideInfo);


            ArchiveData archiveData = ArchiveData.builder()
                    .privateId(privateId)
                    .howToKnow(guideSignupDto.getHowToKnow())
                    .motive(guideSignupDto.getMotive())
                    .privacy(guideSignupDto.isPrivacy())
                    .hopePrefs(guideSignupDto.getHopePrefs())
                    .portraitRights(guideSignupDto.isPortraitRights())
                    .runningPlace(guideSignupDto.getRunningPlace())
                    .build();

            archiveDataRepository.save(archiveData); //기타 데이터 저장

            SignUpInfo signUpInfo = SignUpInfo.builder()
                    .privateId(privateId)
                    .accountId(guideSignupDto.getAccountId())
                    .password(guideSignupDto.getPassword())
                    .build();

            signUpInfo.hashPassword(bCryptPasswordEncoder); //비밀번호 암호화

            signUpInfoRepository.save(signUpInfo); //가입 정보 저장

            SignupResponse response = SignupResponse.builder()
                    .userId(newUser.getUserId())
                    .accessToken(jwtProvider.createAccessToken(privateId))
                    .userStatus(newUser.getRole().getValue())
                    .build();

            return response;
        }
    }
}
