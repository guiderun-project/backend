package com.guide.run.user.service;

import com.guide.run.global.exception.user.authorize.ExistUserException;
import com.guide.run.global.exception.user.dto.BlankRequiredInfoException;
import com.guide.run.global.exception.user.dto.NotAgreeTermException;
import com.guide.run.user.dto.request.SignupRequest;
import com.guide.run.user.dto.response.IntegratedSignupResponse;
import com.guide.run.global.jwt.JwtProvider;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * VI/Guide 구분 없이 하나의 엔드포인트로 처리하는 통합 회원가입 서비스.
 * 기존 ViService/GuideService의 가입 로직을 신규 요청 구조에 맞춰 통합한다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SignupService {
    private final UserRepository userRepository;
    private final ViRepository viRepository;
    private final GuideRepository guideRepository;
    private final ArchiveDataRepository archiveDataRepository;
    private final UserService userService;
    private final JwtProvider jwtProvider;

    @Transactional
    public IntegratedSignupResponse signup(String privateId, SignupRequest request) {
        SignupRequest.Common common = request.getCommon();

        // 필수 약관은 모두 true 여야 함
        if (!common.isPrivacy() || !common.isPortraitRights() || !common.isTrainingSafety()) {
            throw new NotAgreeTermException();
        }

        UserType type = request.getDisabilityType();

        // 기존 사용자 검증: 임시(ROLE_NEW) 사용자만 가입 진행 가능
        User existing = userRepository.findById(privateId).orElse(null);
        if (existing != null && !Role.ROLE_NEW.equals(existing.getRole())) {
            throw new ExistUserException();
        }

        String phoneNumber = userService.extractNumber(common.getPhoneNumber());

        User user = User.builder()
                .userId(userService.getUUID())
                .privateId(privateId)
                .name(common.getName())
                .gender(common.getGender())
                .phoneNumber(phoneNumber)
                .isOpenNumber(common.getIsOpenNumber() != null ? common.getIsOpenNumber() : false)
                .age(0)
                .snsId(common.getSnsId())
                .isOpenSns(common.getIsOpenSns() != null ? common.getIsOpenSns() : false)
                .competitionCnt(0)
                .trainingCnt(0)
                .role(Role.ROLE_WAIT)
                .type(type)
                .birth(common.getBirthDate())
                .build();

        // 임시 사용자 제거 후 신규 사용자 저장
        if (existing != null) {
            userRepository.delete(existing);
            userRepository.flush();
        }

        ArchiveData archiveData;
        if (UserType.VI.equals(type)) {
            SignupRequest.Vi viReq = request.getVi();
            if (viReq == null) {
                throw new BlankRequiredInfoException();
            }
            user.editRunningInfo(viReq.getRunningGroup(), viReq.getDetailRecord());
            userRepository.save(user);

            viRepository.save(Vi.builder()
                    .privateId(privateId)
                    .isRunningExp(viReq.getIsRunningExp())
                    .guideName(viReq.getGuideName())
                    .build());

            archiveData = ArchiveData.builder()
                    .privateId(privateId)
                    .howToKnow(nullSafe(viReq.getHowToKnow()))
                    .motive(viReq.getMotive())
                    .privacy(common.isPrivacy())
                    .hopePrefs(viReq.getHopePrefs())
                    .portraitRights(common.isPortraitRights())
                    .trainingSafety(common.isTrainingSafety())
                    .runningPlace(viReq.getRunningPlace())
                    .build();
        } else if (UserType.GUIDE.equals(type)) {
            SignupRequest.Guide guideReq = request.getGuide();
            if (guideReq == null) {
                throw new BlankRequiredInfoException();
            }
            user.editRunningInfo(guideReq.getRunningGroup(), guideReq.getDetailRecord());
            userRepository.save(user);

            guideRepository.save(Guide.builder()
                    .privateId(privateId)
                    .isGuideExp(guideReq.getIsGuideExp())
                    .viName(guideReq.getViName())
                    .viRecord(guideReq.getViRecord())
                    .viCount(guideReq.getViCount())
                    .guidingPace(guideReq.getGuidingPace())
                    .build());

            archiveData = ArchiveData.builder()
                    .privateId(privateId)
                    .howToKnow(nullSafe(guideReq.getHowToKnow()))
                    .motive(guideReq.getMotive())
                    .privacy(common.isPrivacy())
                    .hopePrefs(guideReq.getHopePrefs())
                    .portraitRights(common.isPortraitRights())
                    .trainingSafety(common.isTrainingSafety())
                    .runningPlace(guideReq.getRunningPlace())
                    .build();
        } else {
            throw new BlankRequiredInfoException();
        }

        archiveDataRepository.save(archiveData);

        // 관리자에게 신규 가입 알림
        userService.signUpATA(privateId);

        return IntegratedSignupResponse.builder()
                .userId(user.getUserId())
                .accessToken(jwtProvider.createAccessToken(privateId))
                .refreshToken(jwtProvider.createRefreshToken(privateId))
                .role(user.getRole().getValue())
                .disabilityType(user.getType())
                .build();
    }

    private List<String> nullSafe(List<String> list) {
        return list != null ? list : new ArrayList<>();
    }
}
