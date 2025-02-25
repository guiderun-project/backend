package com.guide.run.event.service;

import com.guide.run.attendance.service.AttendService;
import com.guide.run.event.entity.Event;
import com.guide.run.event.entity.dto.response.attend.AttendCount;
import com.guide.run.event.entity.dto.response.attend.ParticipationCount;
import com.guide.run.event.entity.dto.response.attend.ParticipationInfos;
import com.guide.run.event.entity.repository.EventRepository;
import com.guide.run.event.entity.type.EventType;
import com.guide.run.global.exception.user.resource.NotExistUserException;
import com.guide.run.partner.entity.matching.Matching;
import com.guide.run.partner.entity.matching.repository.MatchingRepository;
import com.guide.run.partner.entity.partner.Partner;
import com.guide.run.partner.entity.partner.repository.PartnerRepository;
import com.guide.run.attendance.entity.Attendance;
import com.guide.run.attendance.repository.AttendanceRepository;
import com.guide.run.partner.service.PartnerService;
import com.guide.run.user.entity.type.UserType;
import com.guide.run.user.entity.user.User;
import com.guide.run.user.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.xmlbeans.impl.regex.Match;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventAttendService {
    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;
    private final AttendService attendService;
    private final PartnerService partnerService;

    public void requestAttend(Long eventId, String userId) {
        User user = userRepository.findUserByUserId(userId).orElseThrow(NotExistUserException::new);
        Attendance attendance = attendanceRepository.findByEventIdAndPrivateId(eventId, user.getPrivateId());
        if(attendance.isAttend()){
            if(user.getType().equals(UserType.VI)){
                partnerService.setNotAttendViPartnerList(eventId, user);
            }else if(user.getType().equals(UserType.GUIDE)){
                partnerService.setNotAttendGuidePartner(eventId, user);
            }
            attendanceRepository.save(
                    Attendance.builder()
                            .eventId(eventId)
                            .privateId(user.getPrivateId())
                            .isAttend(false)
                            .date(LocalDateTime.now())
                            .build()
            );

        }
        else{
            if(user.getType().equals(UserType.VI)){
                partnerService.setAttendViPartnerList(eventId, user);
            }else if(user.getType().equals(UserType.GUIDE)){
                partnerService.setAttendGuidePartner(eventId, user);
            }
            attendanceRepository.save(
                    Attendance.builder()
                            .eventId(eventId)
                            .privateId(user.getPrivateId())
                            .isAttend(true)
                            .date(LocalDateTime.now())
                            .build()
            );
        }

        //출석 개수 반영
        attendService.countAttendEvent(user.getPrivateId());

    }

    public AttendCount getAttendCount(Long eventId) {
        return AttendCount.builder()
                .attend(attendanceRepository.countByIsAttendAndEventId(true,eventId))
                .notAttend(attendanceRepository.countByIsAttendAndEventId(false,eventId))
                .build();
    }

    public ParticipationCount getParticipationCount(Long eventId) {
        Long guideNum = attendanceRepository.countUserType(eventId, UserType.GUIDE);
        Long ViNum = attendanceRepository.countUserType(eventId, UserType.VI);
        return ParticipationCount.builder()
                .count(guideNum+ViNum)
                .vi(ViNum)
                .guide(guideNum)
                .build();
    }

    public ParticipationInfos getParticipationInfos(Long eventId) {
        return ParticipationInfos.builder()
                .attend(attendanceRepository.getParticipationInfo(eventId,true))
                .notAttend(attendanceRepository.getParticipationInfo(eventId,false))
                .build();
    }

}
