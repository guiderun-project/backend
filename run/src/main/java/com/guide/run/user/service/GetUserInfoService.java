package com.guide.run.user.service;

import com.guide.run.user.dto.response.UserInfoAll.GuideInfoAllResponse;
import com.guide.run.user.dto.response.UserInfoAll.UserInfoAllResponse;
import com.guide.run.user.dto.response.UserInfoAll.ViInfoAllResponse;
import com.guide.run.user.entity.ArchiveData;
import com.guide.run.user.entity.type.Role;
import com.guide.run.user.entity.type.UserType;
import com.guide.run.user.entity.user.Guide;
import com.guide.run.user.entity.user.User;
import com.guide.run.user.entity.user.Vi;
import com.guide.run.user.repository.ArchiveDataRepository;
import com.guide.run.user.repository.GuideRepository;
import com.guide.run.user.repository.ViRepository;
import com.guide.run.user.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetUserInfoService {
    private final UserRepository userRepository;
    private final ArchiveDataRepository archiveDataRepository;
    private final ViRepository viRepository;
    private final GuideRepository guideRepository;

    public UserInfoAllResponse getUserInfoAll(){
        List<User> userList = userRepository.findAll();
        List<GuideInfoAllResponse> guideResponse = new ArrayList<>();
        List<ViInfoAllResponse> viResponse = new ArrayList<>();

        for(User u : userList) {
            ArchiveData archiveData = archiveDataRepository.findById(u.getPrivateId()).orElse(null);

            if (!u.getRole().equals(Role.ROLE_DELETE) || !u.getRole().equals(Role.ROLE_NEW)) {
                if (u.getType().equals(UserType.VI)) {
                    Vi vi = viRepository.findById(u.getPrivateId()).orElse(null);
                    if (archiveData != null && vi != null) {
                        ViInfoAllResponse viInfoAllResponse = ViInfoAllResponse.builder()
                                .name(u.getName())
                                .type(u.getType())
                                .role(u.getRole().getValue().substring(5))
                                .age(u.getAge())
                                .gender(u.getGender())
                                .phoneNumber(u.getPhoneNumber())
                                .recordDegree(u.getRecordDegree())
                                .snsId(u.getSnsId())
                                .isOpenNumber(u.getIsOpenNumber())
                                .isOpenSns(u.getIsOpenSns())
                                .detailRecord(u.getDetailRecord())

                                .isRunningExp(vi.getIsRunningExp())
                                .guideName(vi.getGuideName())

                                .howToKnow(archiveData.getHowToKnow())
                                .motive(archiveData.getMotive())
                                .runningPlace(archiveData.getRunningPlace())
                                .hopePrefs(archiveData.getHopePrefs())

                                .portraitRights(archiveData.isPortraitRights())
                                .privacy(archiveData.isPrivacy())
                                .build();

                        viResponse.add(viInfoAllResponse);
                    }
                } else if (u.getType().equals(UserType.GUIDE)) {
                    Guide guide = guideRepository.findById(u.getPrivateId()).orElse(null);
                    if (archiveData != null && guide != null) {
                        GuideInfoAllResponse guideInfoAllResponse = GuideInfoAllResponse.builder()
                                .name(u.getName())
                                .type(u.getType())
                                .role(u.getRole().getValue().substring(5))
                                .age(u.getAge())
                                .gender(u.getGender())
                                .phoneNumber(u.getPhoneNumber())
                                .recordDegree(u.getRecordDegree())
                                .snsId(u.getSnsId())
                                .isOpenNumber(u.getIsOpenNumber())
                                .isOpenSns(u.getIsOpenSns())
                                .detailRecord(u.getDetailRecord())

                                .isGuideExp(guide.getIsGuideExp())
                                .viName(guide.getViName())
                                .viCount(guide.getViCount())
                                .guidingPace(guide.getGuidingPace())
                                .viRecord(guide.getViRecord())

                                .runningPlace(archiveData.getRunningPlace())
                                .howToKnow(archiveData.getHowToKnow())
                                .motive(archiveData.getMotive())
                                .hopePrefs(archiveData.getHopePrefs())

                                .portraitRights(archiveData.isPortraitRights())
                                .privacy(archiveData.isPrivacy())

                                .build();

                        guideResponse.add(guideInfoAllResponse);
                    }
                }
            }
        }

        return UserInfoAllResponse.builder()
                .guideInfo(guideResponse)
                .viInfo(viResponse)
                .build();
    }
}
