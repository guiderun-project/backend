package com.guide.run.user.service;

import com.guide.run.global.exception.user.authorize.UnauthorizedUserException;
import com.guide.run.global.exception.user.resource.NotExistUserException;
import com.guide.run.user.dto.GuideRunningInfoDto;
import com.guide.run.user.dto.PermissionDto;
import com.guide.run.user.dto.PersonalInfoDto;
import com.guide.run.user.dto.ViRunningInfoDto;
import com.guide.run.user.entity.*;
import com.guide.run.user.entity.type.Role;
import com.guide.run.user.entity.user.Guide;
import com.guide.run.user.entity.user.User;
import com.guide.run.user.entity.user.Vi;
import com.guide.run.user.repository.*;
import com.guide.run.user.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class SignupInfoService {
    private final ViRepository viRepository;
    private final GuideRepository guideRepository;
    private final UserRepository userRepository;
    private final ArchiveDataRepository archiveDataRepository;

    //약관 동의 조회
    @Transactional
    public PermissionDto getPermission(String userId, String privateId){
        User user = userRepository.findUserByUserId(userId).orElseThrow(
                NotExistUserException::new);
        User viewer = userRepository.findById(privateId).orElseThrow(
                NotExistUserException::new
        );

        if(viewer.getRole()!= Role.ROLE_ADMIN && !user.getPrivateId().equals(privateId)){
            throw new UnauthorizedUserException();
        }

        ArchiveData archiveData = archiveDataRepository.findById(user.getPrivateId()).orElseThrow(
                NotExistUserException::new
        );
        
        return PermissionDto.builder()
                .privacy(archiveData.isPrivacy())
                .portraitRights(archiveData.isPortraitRights())
                .build();
    }

    @Transactional
    public PermissionDto editPermission(String privateId, PermissionDto request){
        ArchiveData archiveData = archiveDataRepository.findById(privateId).orElseThrow(
                NotExistUserException::new
        );

        archiveData.editPermisson(
                request.isPrivacy(),
                request.isPortraitRights()
        );

        return PermissionDto.builder()
                .privacy(archiveData.isPrivacy())
                .portraitRights(archiveData.isPortraitRights())
                .build();
    }

    //VI 러닝 스펙 조회
    @Transactional
    public ViRunningInfoDto getViRunningInfo(String userId, String privateId){

        User user = userRepository.findUserByUserId(userId).orElseThrow(
                NotExistUserException::new);

        User viewer = userRepository.findById(privateId).orElseThrow(
                NotExistUserException::new
        );

        if(viewer.getRole()!= Role.ROLE_ADMIN && !user.getPrivateId().equals(privateId)){
            throw new UnauthorizedUserException();
        }

        ArchiveData archiveData = archiveDataRepository.findById(user.getPrivateId()).orElseThrow(
                NotExistUserException::new);
        Vi vi = viRepository.findById(user.getPrivateId()).orElseThrow(
                NotExistUserException::new);


        ViRunningInfoDto response = new ViRunningInfoDto();

        return response.fromEntity(user, vi, archiveData);
    }

    //GUIDE 러닝 스펙 조회
    @Transactional
    public GuideRunningInfoDto getGuideRunningInfo(String userId, String privateId){

        User user = userRepository.findUserByUserId(userId).orElseThrow(
                NotExistUserException::new);
        User viewer = userRepository.findById(privateId).orElseThrow(
                NotExistUserException::new
        );

        if(viewer.getRole()!= Role.ROLE_ADMIN && !user.getPrivateId().equals(privateId)){
            throw new UnauthorizedUserException();
        }

        ArchiveData archiveData = archiveDataRepository.findById(user.getPrivateId()).orElseThrow(
                NotExistUserException::new);
        Guide guide = guideRepository.findById(user.getPrivateId()).orElseThrow(
                NotExistUserException::new);


        GuideRunningInfoDto response = new GuideRunningInfoDto();

        return response.fromEntity(user, guide, archiveData);
    }


    //vi 러닝 스펙 수정
    @Transactional
    public ViRunningInfoDto editViRunningInfo(String privateId, ViRunningInfoDto request){

        User user = userRepository.findById(privateId).orElseThrow(
                NotExistUserException::new
        );
        ArchiveData archiveData = archiveDataRepository.findById(user.getPrivateId()).orElseThrow(
                NotExistUserException::new);
        Vi vi = viRepository.findById(user.getPrivateId()).orElseThrow(
                NotExistUserException::new);



        user.editRunningInfo(request.getRecordDegree(), request.getDetailRecord());
        vi.editViRunningInfo(request.getIsRunningExp(), request.getGuideName());
        archiveData.editRunningInfo(request.getHowToKnow(), request.getMotive(), request.getHopePrefs(), request.getRunningPlace());

        ViRunningInfoDto response = new ViRunningInfoDto();

        return response.fromEntity(user, vi, archiveData);
    }

    //guide 러닝 스펙 수정
    @Transactional
    public GuideRunningInfoDto editGuideRunningInfo(String privateId, GuideRunningInfoDto request){

        User user = userRepository.findById(privateId).orElseThrow(
                NotExistUserException::new
        );
        ArchiveData archiveData = archiveDataRepository.findById(user.getPrivateId()).orElseThrow(
                NotExistUserException::new);
        Guide guide = guideRepository.findById(user.getPrivateId()).orElseThrow(
                NotExistUserException::new);

        user.editRunningInfo(request.getRecordDegree(), request.getDetailRecord());

        guide.editGuideRunningInfo(
                request.getIsGuideExp(),
                request.getViName(),
                request.getViRecord(),
                request.getViCount(),
                request.getGuidingPace());

        archiveData.editRunningInfo(request.getHowToKnow(), request.getMotive(), request.getHopePrefs(), request.getRunningPlace());

        GuideRunningInfoDto response = new GuideRunningInfoDto();

        return response.fromEntity(user, guide, archiveData);
    }

    //개인 정보 조회
    @Transactional
    public PersonalInfoDto getPersonalInfo(String privateId, String userId){
        //역할, 성별, 이름, 전화번호, 나이, sns

        User user = userRepository.findUserByUserId(userId).orElseThrow(
                NotExistUserException::new);
        User viewer = userRepository.findById(privateId).orElseThrow(
                NotExistUserException::new
        );

        if(viewer.getRole()!= Role.ROLE_ADMIN && !user.getPrivateId().equals(privateId)){
            throw new UnauthorizedUserException();
        }
        return PersonalInfoDto.userToInfoDto(user);
    }
    //개인 정보 수정
    @Transactional
    public PersonalInfoDto editPersonalInfo(String privateId, PersonalInfoDto dto){
        //성별, 이름, 전화번호, 나이, sns

        User user = userRepository.findById(privateId).orElseThrow(
                NotExistUserException::new
        );

        if(!user.getPrivateId().equals(privateId)){
            throw new UnauthorizedUserException();
        }

        user.editUser(
                dto.getName(),
                dto.getGender(),
                dto.getPhoneNumber(),
                dto.getIsOpenNumber(),
                dto.getAge(),
                dto.getSnsId(),
                dto.getIsOpenSns(),
                dto.getId1365()
        );

        return PersonalInfoDto.userToInfoDto(user);
    }
}
