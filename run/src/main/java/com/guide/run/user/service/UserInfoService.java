package com.guide.run.user.service;

import com.guide.run.user.dto.PermissionDto;
import com.guide.run.user.dto.PersonalInfoDto;
import com.guide.run.user.dto.ViRunningInfoDto;
import com.guide.run.user.entity.Permission;
import com.guide.run.user.entity.User;
import com.guide.run.user.entity.Vi;
import com.guide.run.user.repository.ArchiveDataRepository;
import com.guide.run.user.repository.PermissionRepository;
import com.guide.run.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserInfoService {
    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final ArchiveDataRepository archiveDataRepository;

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

    //todo : 러닝 스펙 조회, 수정은 따로 공부가 필요할 듯 하다..
    //러닝 스펙 조회
    //러닝 스펙 수정

    //개인 정보 조회
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
