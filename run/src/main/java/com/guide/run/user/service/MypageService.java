package com.guide.run.user.service;

import com.guide.run.admin.dto.EventTypeCountDto;
import com.guide.run.event.entity.dto.response.get.MyPageEvent;
import com.guide.run.event.entity.repository.EventRepository;
import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.global.exception.event.logic.NotValidKindException;
import com.guide.run.global.exception.event.resource.NotExistEventException;
import com.guide.run.global.exception.user.resource.NotExistUserException;
import com.guide.run.partner.entity.partner.PartnerLike;
import com.guide.run.partner.entity.partner.repository.PartnerLikeRepository;
import com.guide.run.partner.entity.dto.MyPagePartner;
import com.guide.run.partner.entity.partner.repository.PartnerRepository;
import com.guide.run.user.dto.GlobalUserInfoDto;
import com.guide.run.user.dto.response.ProfileResponse;
import com.guide.run.user.entity.type.Role;
import com.guide.run.user.entity.user.User;
import com.guide.run.user.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MypageService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final PartnerRepository partnerRepository;
    private final PartnerLikeRepository partnerLikeRepository;

    public GlobalUserInfoDto getGlobalUserInfo(String privateId){
        User user = userRepository.findById(privateId).orElseThrow(
                NotExistUserException::new
        );

        return GlobalUserInfoDto.userToInfoDto(user);
    }

    public long getMyPageEventsCount(String userId, String kind, int year){
        User user = userRepository.findUserByUserId(userId).orElseThrow(
                NotExistUserException::new
        );

        boolean isValidStatus = Arrays.stream(EventRecruitStatus.values())
                .anyMatch(e -> e.name().equals(kind));
        if(isValidStatus){
            throw new NotValidKindException();
        }

        String privateId = user.getPrivateId();
        long count = eventRepository.countMyEventAfterYear(privateId, kind, year);

        return count;
    }

    public List<MyPageEvent> getMyPageEvents(String userId, int start, int limit, String kind, int year){

        User user = userRepository.findUserByUserId(userId).orElseThrow(
                NotExistUserException::new
        );

        boolean isValidStatus = Arrays.stream(EventRecruitStatus.values())
                .anyMatch(e -> e.name().equals(kind));
        if(isValidStatus){
            throw new NotValidKindException();
        }

        String privateId = user.getPrivateId();

        List<MyPageEvent> response = eventRepository.findMyEventAfterYear(privateId, start, limit, kind, year);

        return response;
    }

    public List<MyPagePartner> getMyPagePartners(String userId, int start, int limit, String sort){
        User user = userRepository.findUserByUserId(userId).orElseThrow(
                NotExistUserException::new
        );


        List<MyPagePartner> response = partnerRepository.findMyPartner(user.getPrivateId(),sort , limit, start, user.getType());

        return response;
    }

    public long getMyPartnersCount(String userId){
        User user = userRepository.findUserByUserId(userId).orElseThrow(
                NotExistUserException::new
        );


        long response = partnerRepository.countMyPartner(user.getPrivateId(), user.getType());
        return response;
    }

    public ProfileResponse getUserProfile(String userId, String privateId){
        String phoneNum = "";
        String snsId = "";
        int like = 0;
        User user = userRepository.findUserByUserId(userId).orElseThrow(NotExistEventException::new);
        User viewer = userRepository.findById(privateId).orElseThrow(NotExistUserException::new);
        if(viewer.getRole()== Role.ROLE_ADMIN || viewer.getPrivateId().equals(user.getPrivateId())){
            phoneNum = user.getPhoneNumber();
            snsId = user.getSnsId();
        }

        PartnerLike partnerlike = partnerLikeRepository.findById(privateId).orElse(null);

        if(partnerlike!=null){
            like = partnerlike.getSendIds().size();
        }

        ProfileResponse response = ProfileResponse.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .type(user.getType())
                .gender(user.getGender())
                .recordDegree(user.getRecordDegree())
                .detailRecord(user.getDetailRecord())

                .phoneNumber(phoneNum)
                .isOpenNumber(user.getIsOpenNumber())
                .snsId(snsId)
                .isOpenSns(user.getIsOpenSns())
                .age(user.getAge())
                .totalCnt(user.getCompetitionCnt()+user.getTrainingCnt())
                .competitionCnt(user.getCompetitionCnt())
                .trainingCnt(user.getTrainingCnt())
                .img(user.getImg())
                .like(like)
                .build();
        return response;
    }

    public EventTypeCountDto getMyPageEventTypeCount(String privateId){
        User user = userRepository.findUserByPrivateId(privateId).orElseThrow(NotExistUserException::new);
        return EventTypeCountDto.builder()
                .totalCnt(user.getCompetitionCnt()+user.getTrainingCnt())
                .competitionCnt(user.getCompetitionCnt())
                .trainingCnt(user.getTrainingCnt())
                .build();
    }
}
