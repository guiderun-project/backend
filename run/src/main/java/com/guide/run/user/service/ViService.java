package com.guide.run.user.service;

import com.guide.run.global.exception.user.authorize.ExistUserException;
import com.guide.run.global.jwt.JwtProvider;
import com.guide.run.temp.member.service.TmpService;
import com.guide.run.user.dto.ViSignupDto;
import com.guide.run.user.dto.response.SignupResponse;
import com.guide.run.user.entity.*;
import com.guide.run.user.entity.type.Role;
import com.guide.run.user.entity.type.UserType;
import com.guide.run.user.entity.user.User;
import com.guide.run.user.entity.user.Vi;
import com.guide.run.user.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class ViService {
    private final ViRepository viRepository;
    private final UserRepository userRepository;
    private final ArchiveDataRepository archiveDataRepository;
    private final PermissionRepository permissionRepository;
    private final JwtProvider jwtProvider;
    private final UserService userService;

    private final PasswordEncoder bCryptPasswordEncoder;
    private final SignUpInfoRepository signUpInfoRepository;

    private final TmpService tmpService;

    @Transactional
    public SignupResponse viSignup(String privateId, ViSignupDto viSignupDto){
        User user = userRepository.findById(privateId).orElse(null);
        if(user!=null && !user.getRole().equals(Role.NEW)) {
            //log.info("에러발생");
            throw new ExistUserException(); //기가입자나 이미 정보를 입력한 회원이 재요청한 경우 이므로 에러 코드 추가
        } else {
            String phoneNum = userService.extractNumber(viSignupDto.getPhoneNumber());

            //가입 전 회원정보 연결
            tmpService.updateMember(phoneNum, privateId);

            User vi = User.builder()
                    .userId(userService.getUUID())
                    .privateId(privateId)
                    .name(viSignupDto.getName())
                    .gender(viSignupDto.getGender())
                    .phoneNumber(phoneNum)
                    .openNumber(viSignupDto.isOpenNumber())
                    .age(viSignupDto.getAge())
                    .detailRecord(viSignupDto.getDetailRecord())
                    .recordDegree(viSignupDto.getRecordDegree())
                    .role(Role.WAIT)
                    .type(UserType.VI)
                    .snsId(viSignupDto.getSnsId())
                    .openSns(viSignupDto.isOpenSns())
                    .build();

            Vi viInfo = Vi.builder()
                    .privateId(privateId)
                    .runningExp(viSignupDto.isRunningExp())
                    .guideName(viSignupDto.getGuideName())
                    .build();

            userRepository.delete(userRepository.findById(privateId).orElse(null)); //임시 유저 삭제

            User newVi = userRepository.save(vi);
            viRepository.save(viInfo);

            ArchiveData archiveData = ArchiveData.builder()
                    .privateId(privateId)
                    .howToKnow(viSignupDto.getHowToKnow())
                    .motive(viSignupDto.getMotive())
                    .runningPlace(viSignupDto.getRunningPlace())
                    .build();

            archiveDataRepository.save(archiveData); //안 쓰는 데이터 저장

            Permission permission = Permission.builder()
                    .privateId(privateId)
                    .privacy(viSignupDto.isPrivacy())
                    .portraitRights(viSignupDto.isPortraitRights())
                    .build();

            permissionRepository.save(permission); //약관 동의 저장

            SignUpInfo signUpInfo = SignUpInfo.builder()
                    .privateId(privateId)
                    .accountId(viSignupDto.getAccountId())
                    .password(viSignupDto.getPassword())
                    .build();

            signUpInfo.hashPassword(bCryptPasswordEncoder); //비밀번호 암호화

            signUpInfoRepository.save(signUpInfo); //가입 정보 저장

            SignupResponse response = SignupResponse
                    .builder()
                    .accessToken(jwtProvider.createAccessToken(privateId))
                    .uuid(newVi.getUserId())
                    .userStatus(newVi.getRole().getValue())
                    .build();

            return response;
        }
    }
}
