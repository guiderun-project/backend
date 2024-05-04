package com.guide.run.admin.service;

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
import com.guide.run.user.repository.UserRepository;
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

    public UserListResponse getUserList(int start, int limit){
        int page = start / limit;
        Pageable pageable = PageRequest.of(page, limit, Sort.by("updatedAt").descending());
        Page<User> userList = userRepository.findAllByRoleNot(Role.NEW, pageable);
        List<UserItem> userItems = new ArrayList<>();
        for(User user : userList){
            UserItem item = UserItem.builder()
                    .name(user.getName())
                    .gender(user.getGender())
                    .role(user.getRole())
                    .age(user.getAge())
                    .phoneNumber(user.getPhoneNumber())
                    .snsId(user.getSnsId())
                    .totalCnt(user.getCompetitionCnt() + user.getTrainingCnt())
                    .competitionCnt(user.getCompetitionCnt())
                    .trainingCnt(user.getTrainingCnt())
                    .team(user.getRecordDegree())
                    .type(user.getType())
                    .userId(user.getUserId())
                    .update_date(user.getUpdatedAt().toLocalDate())
                    .update_time(timeFormatter.getHHMMSS(user.getUpdatedAt()))
                    .build();
            userItems.add(item);
        }

        UserListResponse response = UserListResponse.builder()
                .items(userItems)
                .limit(limit)
                .start(start)
                .build();
        return response;
    }

    public Count getUserListCount(){
        Count response = Count.builder()
                .count(userRepository.findAllByRoleNot(Role.NEW).size())
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
            user.approveUser(Role.USER, request.getRecordDegree());
            userRepository.save(user);
            isApprove = true;
        }else{
            user.approveUser(Role.REJECT, user.getRecordDegree());
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
