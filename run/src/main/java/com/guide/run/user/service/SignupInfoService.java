package com.guide.run.user.service;

import com.guide.run.user.dto.GuideRunningInfoDto;
import com.guide.run.user.dto.PermissionDto;
import com.guide.run.user.dto.PersonalInfoDto;
import com.guide.run.user.dto.ViRunningInfoDto;
import com.guide.run.user.entity.*;
import com.guide.run.user.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
@Slf4j
public class SignupInfoService {
    private final ViRepository viRepository;
    private final GuideRepository guideRepository;
    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final ArchiveDataRepository archiveDataRepository;

    @Transactional
    public PermissionDto getPermission(String userId){
        Permission permission = permissionRepository.findById(userId).orElseThrow(
                () -> new NoSuchElementException("유저 정보를 찾을 수 없습니다.")
        );

        PermissionDto response = PermissionDto.builder()
                .privacy(permission.isPrivacy())
                .portraitRights(permission.isPortraitRights())
                .build();

        return response;
    }

    //러닝 스펙 조회
    @Transactional
    public ViRunningInfoDto getViRunningInfo(String userId, String privateId){

        User user = userRepository.findUserByUuid(userId).orElseThrow(
                () -> new NoSuchElementException("유저 정보를 찾을 수 없습니다."));

        if(!user.getPrivateId().equals(privateId)){
            throw new RuntimeException("유저 정보가 일치하지 않음"); //todo : 에러코드 추가해야 함.
        }

        ArchiveData archiveData = archiveDataRepository.findById(user.getPrivateId()).orElseThrow(
                () -> new NoSuchElementException("아카이브 데이터를 찾을 수 없습니다."));
        Vi vi = viRepository.findById(user.getPrivateId()).orElseThrow(
                () -> new NoSuchElementException("vi 정보를 찾을 수 없습니다."));


        ViRunningInfoDto response = new ViRunningInfoDto();

        return response.fromEntity(user, vi, archiveData);
    }

    @Transactional
    public GuideRunningInfoDto getGuideRunningInfo(String userId, String privateId){

        User user = userRepository.findUserByUuid(userId).orElseThrow(
                () -> new NoSuchElementException("유저 정보를 찾을 수 없습니다."));

        if(!user.getPrivateId().equals(privateId)){
            throw new RuntimeException("유저 정보가 일치하지 않음"); //todo : 에러코드 추가해야 함.
        }

        ArchiveData archiveData = archiveDataRepository.findById(user.getPrivateId()).orElseThrow(
                () -> new NoSuchElementException("아카이브 데이터를 찾을 수 없습니다."));
        Guide guide = guideRepository.findById(user.getPrivateId()).orElseThrow(
                () -> new NoSuchElementException("vi 정보를 찾을 수 없습니다."));


        GuideRunningInfoDto response = new GuideRunningInfoDto();

        return response.fromEntity(user, guide, archiveData);
    }


    //vi 러닝 스펙 수정
    @Transactional
    public ViRunningInfoDto editViRunningInfo(String privateId, ViRunningInfoDto request){
        User user = userRepository.findById(privateId).orElseThrow(
                () -> new NoSuchElementException("유저 정보를 찾을 수 없습니다.")
        );
        ArchiveData archiveData = archiveDataRepository.findById(user.getPrivateId()).orElseThrow(
                () -> new NoSuchElementException("아카이브 데이터를 찾을 수 없습니다."));
        Vi vi = viRepository.findById(user.getPrivateId()).orElseThrow(
                () -> new NoSuchElementException("vi 정보를 찾을 수 없습니다."));

        user.editRunningInfo(request.getRecordDegree(), request.getDetailRecord());
        vi.editViRunningInfo(request.isRunningExp());
        archiveData.editRunningInfo(request.getHowToKnow(), request.getMotive(), request.getHopePrefs());

        ViRunningInfoDto response = new ViRunningInfoDto();

        return response.fromEntity(user, vi, archiveData);
    }

    //guide 러닝 스펙 수정
    @Transactional
    public GuideRunningInfoDto editGuideRunningInfo(String privateId, GuideRunningInfoDto request){
        User user = userRepository.findById(privateId).orElseThrow(
                () -> new NoSuchElementException("유저 정보를 찾을 수 없습니다.")
        );
        ArchiveData archiveData = archiveDataRepository.findById(user.getPrivateId()).orElseThrow(
                () -> new NoSuchElementException("아카이브 데이터를 찾을 수 없습니다."));
        Guide guide = guideRepository.findById(user.getPrivateId()).orElseThrow(
                () -> new NoSuchElementException("vi 정보를 찾을 수 없습니다."));

        user.editRunningInfo(request.getRecordDegree(), request.getDetailRecord());

        guide.editGuideRunningInfo(
                request.isGuideExp(),
                request.getViName(),
                request.getViRecord(),
                request.getViCount(),
                request.getGuidingPace());

        archiveData.editRunningInfo(request.getHowToKnow(), request.getMotive(), request.getHopePrefs());

        GuideRunningInfoDto response = new GuideRunningInfoDto();

        return response.fromEntity(user, guide, archiveData);
    }

    //개인 정보 조회
    @Transactional
    public PersonalInfoDto getPersonalInfo(String userId){
        //역할, 성별, 이름, 전화번호, 나이, sns

        User user = userRepository.findById(userId).orElseThrow(
                () -> new NoSuchElementException("유저 정보를 찾을 수 없습니다.")
        );

        PersonalInfoDto response = PersonalInfoDto.builder()
                .role(user.getRole().getValue())
                .name(user.getName())
                .gender(user.getGender())
                .phoneNumber(user.getPhoneNumber())
                .openNumber(user.isOpenNumber())
                .age(user.getAge())
                .snsId(user.getSnsId())
                .openSns(user.isOpenSns())
                .build();

        return response;
    }
    //개인 정보 수정
    @Transactional
    public PersonalInfoDto editPersonalInfo(String userId, PersonalInfoDto dto){
        //성별, 이름, 전화번호, 나이, sns

        User user = userRepository.findById(userId).orElseThrow(
                () -> new NoSuchElementException("유저 정보를 찾을 수 없습니다.")
        );

        user.editUser(
                dto.getName(),
                dto.getGender(),
                dto.getPhoneNumber(),
                dto.isOpenNumber(),
                dto.getAge(),
                dto.getSnsId(),
                dto.isOpenSns()
        );

        PersonalInfoDto response = PersonalInfoDto.userToInfoDto(user);

        return response;
    }
}
