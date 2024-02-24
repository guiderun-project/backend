package com.guide.run.user.service;

import com.guide.run.event.entity.Event;
import com.guide.run.event.entity.EventForm;
import com.guide.run.event.entity.dto.response.search.MyPageEvent;
import com.guide.run.event.entity.dto.response.search.MyPageEventResponse;
import com.guide.run.event.entity.dto.response.search.UpcomingEvent;
import com.guide.run.event.entity.repository.EventFormRepository;
import com.guide.run.event.entity.repository.EventRepository;
import com.guide.run.global.exception.event.resource.NotExistEventException;
import com.guide.run.global.exception.user.resource.NotExistUserException;
import com.guide.run.user.dto.GlobalUserInfoDto;
import com.guide.run.user.dto.response.ProfileResponse;
import com.guide.run.user.entity.type.Role;
import com.guide.run.user.entity.user.User;
import com.guide.run.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MypageService {
    private final UserRepository userRepository;
    private final EventFormRepository eventFormRepository;
    private final EventRepository eventRepository;

    public GlobalUserInfoDto getGlobalUserInfo(String privateId){
        User user = userRepository.findById(privateId).orElseThrow(
                NotExistUserException::new
        );

        return GlobalUserInfoDto.userToInfoDto(user);
    }

    public int getMyPageEventsCount(String userId){
        User user = userRepository.findUserByUserId(userId).orElseThrow(
                NotExistUserException::new
        );
        int count = eventFormRepository.findAllByPrivateId(user.getPrivateId()).size();

        return count;
    }
    public MyPageEventResponse getMyPageEvents(String userId, int start, int limit){
        User user = userRepository.findUserByUserId(userId).orElseThrow(
                NotExistUserException::new
        );

        String privateId = user.getPrivateId();
        List<EventForm> eventForms =eventFormRepository.findAllByPrivateId(privateId);
        List<MyPageEvent> events = new ArrayList<>();
        
        //날짜 필터링 없이 조회
        for(EventForm eventForm : eventForms){
             Event event = eventRepository.findById(eventForm.getEventId()).orElseThrow(
                     NotExistEventException::new
             );
             MyPageEvent myPageEvent = MyPageEvent.builder()
                     .eventId(event.getId())
                     .eventType(event.getType())
                     .date(LocalDate.from(event.getStartTime()))
                     .name(event.getName())
                     .recruitStatus(event.getRecruitStatus())
                     .build();
             events.add(myPageEvent);
        }
        events.sort((e1, e2) -> e2.getDate().compareTo(e1.getDate()));


        int startIndex = Math.min(start * limit, events.size());
        int endIndex = Math.min(startIndex + limit, events.size());
        List<MyPageEvent> pagedEvents = events.subList(startIndex, endIndex);

        MyPageEventResponse response = MyPageEventResponse.builder()
                .items(pagedEvents)
                .limit(limit)
                .start(start)
                .build();

        return response;
    }

    public ProfileResponse getUserProfile(String userId, String privateId){
        String phoneNum = "";
        String snsId = "";
        User user = userRepository.findUserByUserId(userId).orElseThrow(NotExistEventException::new);
        User viewer = userRepository.findById(privateId).orElseThrow(NotExistUserException::new);
        if(viewer.getRole()== Role.ADMIN || viewer.getPrivateId().equals(user.getPrivateId())){
            phoneNum = user.getPhoneNumber();
            snsId = user.getSnsId();
        }

        ProfileResponse response = ProfileResponse.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .type(user.getType())
                .gender(user.getGender())
                .recordDegree(user.getRecordDegree())
                .detailRecord(user.getDetailRecord())

                .phoneNumber(phoneNum)
                .isOpenNumber(user.isOpenNumber())
                .snsId(snsId)
                .isOpenSns(user.isOpenSns())
                .age(user.getAge())
                .totalCnt(user.getCompetitionCnt()+user.getTrainingCnt())
                .competitionCnt(user.getCompetitionCnt())
                .trainingCnt(user.getTrainingCnt())
                .build();
        return response;
    }
}
