package com.guide.run.event.service;

import com.guide.run.event.entity.dto.response.attend.AttendCount;
import com.guide.run.event.entity.dto.response.attend.ParticipationCount;
import com.guide.run.event.entity.dto.response.attend.ParticipationInfos;
import com.guide.run.global.exception.user.resource.NotExistUserException;
import com.guide.run.temp.member.entity.Attendance;
import com.guide.run.temp.member.repository.AttendanceRepository;
import com.guide.run.user.entity.type.UserType;
import com.guide.run.user.entity.user.User;
import com.guide.run.user.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EventAttendService {
    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;
    public void requestAttend(Long eventId, String userId) {
        User user = userRepository.findUserByUserId(userId).orElseThrow(NotExistUserException::new);
        Attendance attendance = attendanceRepository.findByEventIdAndPrivateId(eventId, user.getPrivateId());
        if(attendance.isAttend()){
            attendanceRepository.save(
                    Attendance.builder()
                            .eventId(eventId)
                            .privateId(user.getPrivateId())
                            .isAttend(false)
                            .date(null)
                            .build()
            );
        }
        else{
            attendanceRepository.save(
                    Attendance.builder()
                            .eventId(eventId)
                            .privateId(user.getPrivateId())
                            .isAttend(true)
                            .date(LocalDateTime.now())
                            .build()
            );
        }
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
