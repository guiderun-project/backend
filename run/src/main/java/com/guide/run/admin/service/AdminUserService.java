package com.guide.run.admin.service;

import com.guide.run.admin.dto.condition.UserSortCond;
import com.guide.run.admin.dto.request.ApproveRequest;
import com.guide.run.admin.dto.response.GuideApplyResponse;
import com.guide.run.admin.dto.response.UserApprovalResponse;
import com.guide.run.admin.dto.response.ViApplyResponse;
import com.guide.run.admin.dto.response.user.NewUserResponse;
import com.guide.run.admin.dto.response.user.UserItem;
import com.guide.run.event.entity.dto.response.get.Count;
import com.guide.run.global.converter.TimeFormatter;
import com.guide.run.global.exception.user.resource.NotExistUserException;
import com.guide.run.global.jwt.JwtProvider;
import com.guide.run.global.sms.cool.CoolSmsService;
import com.guide.run.user.entity.ArchiveData;
import com.guide.run.user.entity.type.Role;
import com.guide.run.user.entity.user.Guide;
import com.guide.run.user.entity.user.User;
import com.guide.run.user.entity.user.Vi;
import com.guide.run.user.repository.ArchiveDataRepository;
import com.guide.run.user.repository.GuideRepository;
import com.guide.run.user.repository.ViRepository;
import com.guide.run.user.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final JwtProvider jwtProvider;
    private final CoolSmsService coolSmsService;

    public List<UserItem> getUserList(int start, int limit, UserSortCond cond){
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

    //todo : 승인 후 회원에게 알림톡 전송
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

        coolSmsService.sendToNewUser(user.getPhoneNumber(), user.getName(),user.getType().getValue(), user.getRecordDegree());
        return response;
    }

    @Transactional
    public List<NewUserResponse> getNewUser(int start, int limit, String privateId){
        List<NewUserResponse> result = userRepository.findNewUser(start, limit, privateId);
        return result;
    }

    @Transactional
    public List<UserItem> searchUser(int start, int limit, UserSortCond cond, String text){
        List<UserItem> result = userRepository.searchAdminUser(start, limit, cond, text);
        return result;
    }

    @Transactional
    public Count searchUserCount(String text){
        long count = userRepository.searchAdminUserCount(text);

        return Count.builder()
                .count(count)
                .build();
    }

}
