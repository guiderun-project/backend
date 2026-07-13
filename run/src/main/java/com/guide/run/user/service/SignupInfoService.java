package com.guide.run.user.service;

import com.guide.run.global.exception.user.authorize.UnauthorizedUserException;
import com.guide.run.global.exception.user.resource.NotExistUserException;
import com.guide.run.event.entity.Event;
import com.guide.run.event.entity.EventForm;
import com.guide.run.event.entity.repository.EventFormRepository;
import com.guide.run.event.entity.repository.EventRepository;
import com.guide.run.event.entity.type.EventFormStatus;
import com.guide.run.event.entity.type.EventType;
import com.guide.run.user.dto.GuideRunningInfoDto;
import com.guide.run.user.dto.PermissionDto;
import com.guide.run.user.dto.PersonalInfoDto;
import com.guide.run.user.dto.ViRunningInfoDto;
import com.guide.run.user.dto.request.UpdatePersonalInfoRequest;
import com.guide.run.user.dto.request.UpdateRunningInfoRequest;
import com.guide.run.user.dto.response.MyPageResponse;
import com.guide.run.user.dto.response.UpdatePersonalInfoResponse;
import com.guide.run.user.dto.response.UpdateRunningInfoResponse;
import com.guide.run.user.dto.response.UserBirthDatePatchResponse;
import com.guide.run.user.entity.type.UserType;
import com.guide.run.global.exception.user.dto.InvalidItemErrorException;
import com.guide.run.user.entity.*;
import com.guide.run.user.entity.type.Role;
import com.guide.run.user.entity.user.Guide;
import com.guide.run.user.entity.user.User;
import com.guide.run.user.entity.user.Vi;
import com.guide.run.user.repository.*;
import com.guide.run.user.repository.user.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
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
    private final SignUpInfoRepository signUpInfoRepository;
    private final EventFormRepository eventFormRepository;
    private final EventRepository eventRepository;

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
                .trainingSafety(archiveData.isTrainingSafety())
                .build();
    }

    @Transactional(readOnly = true)
    public PermissionDto getMyPermission(String privateId) {
        userRepository.findById(privateId).orElseThrow(NotExistUserException::new);
        ArchiveData archiveData = archiveDataRepository.findById(privateId).orElseThrow(
                NotExistUserException::new
        );

        return toPermissionDto(archiveData);
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
                .trainingSafety(archiveData.isTrainingSafety())
                .build();
    }

    @Transactional
    public PermissionDto agreeTrainingSafety(String privateId) {
        ArchiveData archiveData = archiveDataRepository.findById(privateId).orElseThrow(
                NotExistUserException::new
        );

        archiveData.agreeTrainingSafety();
        return toPermissionDto(archiveData);
    }

    private PermissionDto toPermissionDto(ArchiveData archiveData) {
        return PermissionDto.builder()
                .privacy(archiveData.isPrivacy())
                .portraitRights(archiveData.isPortraitRights())
                .trainingSafety(archiveData.isTrainingSafety())
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
                dto.getId1365(),
                dto.getBirth()
        );

        return PersonalInfoDto.userToInfoDto(user);
    }

    @Transactional
    public UserBirthDatePatchResponse updateBirthDate(String privateId, String birthDate) {
        User user = userRepository.findById(privateId).orElseThrow(NotExistUserException::new);
        user.editBirthDate(birthDate);
        return UserBirthDatePatchResponse.builder()
                .birthDate(user.getBirth())
                .build();
    }

    @Transactional
    public UpdateRunningInfoResponse updateRunningInfo(String privateId, UpdateRunningInfoRequest request) {
        User user = userRepository.findById(privateId).orElseThrow(NotExistUserException::new);
        // ArchiveData가 없는 사용자(소셜 로그인 초기 상태)도 러닝 정보 수정이 가능하도록 없으면 새로 생성한다.
        ArchiveData archiveData = archiveDataRepository.findById(privateId)
                .orElseGet(() -> ArchiveData.builder().privateId(privateId).build());

        user.editRunningInfo(request.getRecordDegree(), request.getDetailRecord());
        archiveData.editRunningInfo(
                archiveData.getHowToKnow(),
                archiveData.getMotive(),
                request.getHopePrefs(),
                archiveData.getRunningPlace()
        );
        archiveDataRepository.save(archiveData);

        return UpdateRunningInfoResponse.builder()
                .type(user.getType() != null ? user.getType().name() : null)
                .recordDegree(user.getRecordDegree())
                .detailRecord(user.getDetailRecord())
                .hopePrefs(archiveData.getHopePrefs())
                .build();
    }

    @Transactional
    public UpdatePersonalInfoResponse updatePersonalInfo(String privateId, UpdatePersonalInfoRequest request) {
        User user = userRepository.findById(privateId).orElseThrow(NotExistUserException::new);

        if (UserType.VI.equals(user.getType()) && request.getId1365() != null) {
            throw new InvalidItemErrorException();
        }

        user.editPersonalFields(
                request.getPhoneNumber(),
                request.getSnsId(),
                request.getId1365(),
                request.getBirthDate()
        );

        return UpdatePersonalInfoResponse.builder()
                .birthDate(user.getBirth())
                .phoneNumber(user.getPhoneNumber())
                .snsId(user.getSnsId())
                .id1365(user.getId1365())
                .build();
    }

    @Transactional(readOnly = true)
    public MyPageResponse getMyPage(String privateId) {
        User user = userRepository.findById(privateId).orElseThrow(NotExistUserException::new);
        // ArchiveData가 없는 사용자(소셜 로그인 초기 상태)도 마이페이지 조회 가능하도록 null 허용
        ArchiveData archiveData = archiveDataRepository.findById(privateId).orElse(null);
        String accountId = signUpInfoRepository.findById(privateId).map(s -> s.getAccountId()).orElse(null);

        MyPageResponse.Profile profile = MyPageResponse.Profile.builder()
                .name(user.getName())
                .gender(user.getGender())
                .type(user.getType() != null ? user.getType().name() : null)
                .recordDegree(user.getRecordDegree())
                .build();

        MyPageResponse.Participation participation = getParticipation(privateId);

        MyPageResponse.PersonalInfo personalInfo = MyPageResponse.PersonalInfo.builder()
                .birthDate(user.getBirth())
                .phoneNumber(user.getPhoneNumber())
                .snsId(user.getSnsId())
                .id1365(user.getId1365())
                .accountId(accountId)
                .build();

        MyPageResponse.RunningInfo runningInfo = MyPageResponse.RunningInfo.builder()
                .type(user.getType() != null ? user.getType().name() : null)
                .recordDegree(user.getRecordDegree())
                .detailRecord(user.getDetailRecord())
                .hopePrefs(archiveData != null ? archiveData.getHopePrefs() : null)
                .build();

        return MyPageResponse.builder()
                .profile(profile)
                .participation(participation)
                .personalInfo(personalInfo)
                .runningInfo(runningInfo)
                .build();
    }

    private MyPageResponse.Participation getParticipation(String privateId) {
        List<Long> eventIds = eventFormRepository.findAllByPrivateId(privateId).stream()
                .filter(form -> EventFormStatus.APPLIED.equals(form.getStatus()))
                .map(EventForm::getEventId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        if (eventIds.isEmpty()) {
            return participation(0, 0);
        }

        Map<Long, EventType> eventTypesById = eventRepository.findAllById(eventIds).stream()
                .collect(Collectors.toMap(Event::getId, Event::getType, (first, second) -> first));

        int trainingCount = 0;
        int competitionCount = 0;
        for (Long eventId : eventIds) {
            EventType type = eventTypesById.get(eventId);
            if (EventType.TRAINING.equals(type)) {
                trainingCount++;
            } else if (EventType.COMPETITION.equals(type)) {
                competitionCount++;
            }
        }

        return participation(trainingCount, competitionCount);
    }

    private MyPageResponse.Participation participation(int trainingCount, int competitionCount) {
        return MyPageResponse.Participation.builder()
                .trainingCount(trainingCount)
                .competitionCount(competitionCount)
                .totalCount(trainingCount + competitionCount)
                .build();
    }
}
