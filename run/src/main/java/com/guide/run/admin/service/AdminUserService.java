package com.guide.run.admin.service;

import com.guide.run.admin.dto.condition.UserSortCond;
import com.guide.run.admin.dto.request.ApproveRequest;
import com.guide.run.admin.dto.response.*;
import com.guide.run.event.entity.dto.response.get.Count;
import com.guide.run.global.converter.TimeFormatter;
import com.guide.run.global.exception.user.resource.NotExistUserException;
import com.guide.run.user.entity.ArchiveData;
import com.guide.run.user.entity.type.Role;
import com.guide.run.user.entity.user.Guide;
import com.guide.run.user.entity.user.User;
import com.guide.run.user.entity.user.Vi;
import com.guide.run.user.repository.ArchiveDataRepository;
import com.guide.run.user.repository.GuideRepository;
import com.guide.run.user.repository.user.UserRepository;
import com.guide.run.user.repository.ViRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminUserService {
    private final UserRepository userRepository;
    private final ViRepository viRepository;
    private final GuideRepository guideRepository;
    private final ArchiveDataRepository archiveDataRepository;
    private final TimeFormatter timeFormatter;

    public List<UserItem> getUserList(int start, int limit,  boolean time, boolean type, boolean gender, boolean name_team, boolean approval){
        UserSortCond cond = UserSortCond.builder()
                .approval(approval)
                .name_team(name_team)
                .gender(gender)
                .time(time)
                .type(type)
                .build();

       List<UserItem> response =  userRepository.sortAdminUser(start, limit, cond);
        return response;
    }

    public Count getUserListCount(){
        Count response = Count.builder()
                .count(userRepository.sortAdminUserCount())
                .build();
        return response;
    }

    public ViApplyResponse getApplyVi(String userId){
        User user = userRepository.findUserByUserId(userId).orElseThrow(NotExistUserException::new);
        String privateId = user.getPrivateId();
        Vi vi = viRepository.findById(privateId).orElseThrow(NotExistUserException::new);
        ArchiveData archiveData = archiveDataRepository.findById(privateId).orElseThrow(NotExistUserException::new);

        ViApplyResponse response = ViApplyResponse.builder()
                .phoneNumber(user.getPhoneNumber())
                .age(user.getAge())
                .snsId(user.getSnsId())

                .isRunningExp(vi.getIsRunningExp())
                .detailRecord(user.getDetailRecord())
                .recordDegree(user.getRecordDegree())
                .guideName(vi.getGuideName())

                .hopePrefs(archiveData.getHopePrefs())

                .howToKnow(archiveData.getHowToKnow())
                .motive(archiveData.getMotive())
                .privacy(archiveData.isPrivacy())
                .portraitRights(archiveData.isPortraitRights())
                .build();
     return response;
    }

    public GuideApplyResponse getApplyGuide(String userId){
        User user = userRepository.findUserByUserId(userId).orElseThrow(NotExistUserException::new);
        String privateId = user.getPrivateId();
        Guide guide = guideRepository.findById(privateId).orElseThrow(NotExistUserException::new);
        ArchiveData archiveData = archiveDataRepository.findById(privateId).orElseThrow(NotExistUserException::new);

        GuideApplyResponse response = GuideApplyResponse.builder()
                .phoneNumber(user.getPhoneNumber())
                .age(user.getAge())
                .snsId(user.getSnsId())

                .isGuideExp(guide.getIsGuideExp())
                .recordDegree(user.getRecordDegree())
                .detailRecord(user.getDetailRecord())
                .viCount(guide.getViCount())
                .guidingPace(guide.getGuidingPace())
                .runningPlace(archiveData.getRunningPlace())
                .hopePrefs(archiveData.getHopePrefs())

                .howToKnow(archiveData.getHowToKnow())
                .motive(archiveData.getMotive())

                .privacy(archiveData.isPrivacy())
                .snsId(user.getSnsId())
                .build();
        return response;
    }

    @Transactional
    public UserApprovalResponse approveUser(String userId, ApproveRequest request){
        User user = userRepository.findUserByUserId(userId).orElseThrow(NotExistUserException::new);
        Boolean isApprove = false;
        if(request.getIsApprove()){
            user.approveUser(Role.ROLE_USER, request.getRecordDegree());
            userRepository.save(user);
            isApprove = true;
        }else{
            user.approveUser(Role.ROLE_REJECT, user.getRecordDegree());
            userRepository.save(user);
        }
        UserApprovalResponse response = UserApprovalResponse.builder()
                .userId(user.getUserId())
                .isApprove(isApprove)
                .recordDegree(user.getRecordDegree())
                .build();
        return response;
    }

}
