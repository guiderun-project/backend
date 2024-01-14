package com.guide.run.user.service;

import com.guide.run.user.dto.PermissionDto;
import com.guide.run.user.entity.Permission;
import com.guide.run.user.repository.ArchiveDataRepository;
import com.guide.run.user.repository.PermissionRepository;
import com.guide.run.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserInfoService {
    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final ArchiveDataRepository archiveDataRepository;

    public PermissionDto getPermission(String userId){
        Permission permission = permissionRepository.findById(userId).orElseThrow(
                () -> new NoSuchElementException()
        );

        PermissionDto response = PermissionDto.builder()
                .privacy(permission.isPrivacy())
                .portraitRights(permission.isPortraitRights())
                .build();

        return response;
    }
}
