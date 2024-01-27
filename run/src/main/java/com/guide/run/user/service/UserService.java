package com.guide.run.user.service;

import com.guide.run.global.exception.user.dto.InvalidItemErrorException;
import com.guide.run.global.jwt.JwtProvider;
import com.guide.run.user.dto.GuideSignupDto;
import com.guide.run.user.dto.ViSignupDto;
import com.guide.run.user.dto.response.SignupResponse;
import com.guide.run.user.entity.*;
import com.guide.run.user.entity.type.Role;
import com.guide.run.user.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@RequiredArgsConstructor
@Service
@Slf4j
public class UserService {
    private final ViRepository viRepository;
    private final GuideRepository guideRepository;
    private final UserRepository userRepository;
    private final ArchiveDataRepository archiveDataRepository;
    private final PermissionRepository permissionRepository;
    private final JwtProvider jwtProvider;

    public String getUserStatus(String userId){
        String reAssignSocialId = reAssignSocialId(userId);
        User user = userRepository.findById(userId).orElse(null);
        if(user != null){
                return user.getRole().getValue();
        }else{
            //신규 가입자의 경우 인증을 위해 임시 유저 생성
            //가입이 완료되면 새 토큰 다시 줘야함
            userRepository.save(User.builder()
                    .userId(userId)
                    .role(Role.NEW)
                    .userId(getUUID())
                    .build());
            return Role.NEW.getValue();
        }
    }

    @Transactional
    public SignupResponse viSignup(String privateId, ViSignupDto viSignupDto){
        User user = userRepository.findById(reAssignSocialId(privateId)).orElse(null);
        if(user!=null) {
            log.info("에러발생");
            return null; //기가입자나 이미 정보를 입력한 회원이 재요청한 경우 이므로 에러 코드 추가
        } else {
            if(
                    (!viSignupDto.getHowToKnow().isEmpty() || !viSignupDto.getMotive().isBlank())
                    && viSignupDto.isRunningExp()
            ){
                throw new InvalidItemErrorException();
            }
            User vi = User.builder()
                    .userId(getUUID())
                    .userId(privateId)
                    .name(viSignupDto.getName())
                    .gender(viSignupDto.getGender())
                    .phoneNumber(viSignupDto.getPhoneNumber())
                    .openNumber(viSignupDto.isOpenNumber())
                    .age(viSignupDto.getAge())
                    .detailRecord(viSignupDto.getDetailRecord())
                    .recordDegree(viSignupDto.getRecordDegree())
                    .role(Role.WAIT)
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

            SignupResponse response = SignupResponse
                    .builder()
                    .accessToken(jwtProvider.createAccessToken(privateId))
                    .uuid(newVi.getUserId())
                    .userStatus(newVi.getRole().getValue())
                    .build();

            return response;
        }
    }

    @Transactional
    public SignupResponse guideSignup(String privateId, GuideSignupDto guideSignupDto){
        User user = userRepository.findById(reAssignSocialId(privateId)).orElse(null);
        if(user!=null) {
            log.info("에러발생");
            return null;
        } else {
            if(
                    (!guideSignupDto.getHowToKnow().isEmpty() || !guideSignupDto.getMotive().isBlank())
                            && guideSignupDto.isGuideExp()
            ){
                throw new InvalidItemErrorException();
            }

            User guide = User.builder()
                    .userId(getUUID())
                    .privateId(privateId)
                    .name(guideSignupDto.getName())
                    .gender(guideSignupDto.getGender())
                    .phoneNumber(guideSignupDto.getPhoneNumber())
                    .openNumber(guideSignupDto.isOpenNumber())
                    .age(guideSignupDto.getAge())
                    .detailRecord(guideSignupDto.getDetailRecord())
                    .recordDegree(guideSignupDto.getRecordDegree())
                    .snsId(guideSignupDto.getSnsId())
                    .openSns(guideSignupDto.isOpenSns())
                    .role(Role.WAIT)
                    .build();

            Guide guideInfo = Guide.builder()
                    .privateId(privateId)
                    .guideExp(guideSignupDto.isGuideExp())
                    .viName(guideSignupDto.getViName())
                    .viCount(guideSignupDto.getViCount())
                    .viRecord(guideSignupDto.getViRecord())
                    .build();


            userRepository.delete(userRepository.findById(privateId).orElse(null)); //임시 유저 삭제

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

            //todo : 추후 일반 로그인을 위한 SignupInfo도 생성해야 함.

            SignupResponse response = SignupResponse.builder()
                    .uuid(newUser.getUserId())
                    .accessToken(jwtProvider.createAccessToken(privateId))
                    .userStatus(newUser.getRole().getValue())
                    .build();

            return response;
        }
    }


    public String getUUID(){
        String id = UUID.randomUUID().toString();
        return id;
    }


    //userId = privateId로 변경, uuid -> userId 로 변경
    //기가입자 및 이미 정보를 입력한 회원은 재할당 하지 않음
    private String reAssignSocialId(String privateId) {
        if(privateId.startsWith("kakao_")){
            return privateId;
        }else if(privateId.startsWith("kakao")){
            return "kakao_"+privateId.substring(6);
        }
        else{
            return "Error";
        }
    }

    //임시 자격 부여
    public void temporaryUserCreate(String socialId){
        User user = User.builder()
                .privateId(socialId)
                .role(Role.NEW)
                .build();
        userRepository.save(user);
    }

}
