package com.guide.run.attendance.service;

import com.guide.run.attendance.entity.Attendance;
import com.guide.run.attendance.repository.AttendanceRepository;
import com.guide.run.event.entity.Event;
import com.guide.run.event.entity.repository.EventRepository;
import com.guide.run.event.entity.type.EventType;
import com.guide.run.global.exception.event.resource.NotExistEventException;
import com.guide.run.global.exception.user.resource.NotExistUserException;
import com.guide.run.user.entity.type.UserType;
import com.guide.run.user.entity.user.User;
import com.guide.run.user.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AttendService {

    private final AttendanceRepository attendanceRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Transactional
    public void countAttendUser(long eventId, boolean isAttend) {
        int viCnt = 0;
        int guideCnt = 0;
        Event event = eventRepository.findById(eventId).orElseThrow(NotExistEventException::new);
        List<Attendance> attendances = attendanceRepository.findAllByEventIdAndIsAttend(eventId, isAttend);
        for(Attendance attendance : attendances) {
            User user = userRepository.findById(attendance.getPrivateId()).orElse(null);
            if(user != null && user.getType() != null) {
                if(user.getType().equals(UserType.VI)){
                    viCnt++;
                }else if(user.getType().equals(UserType.GUIDE)){
                    guideCnt++;
                }
            }
        }

        event.countAttendGAndV(guideCnt,viCnt);

        eventRepository.save(event);
    }

    @Transactional
    public void countAttendEvent(String privateId) {
        int traingCnt = 0;
        int compCnt = 0;

        boolean isAttend = true;

        User user = userRepository.findById(privateId).orElseThrow(NotExistUserException::new);
        List<Attendance> attendances = attendanceRepository.findAllByPrivateIdAndIsAttend(privateId, isAttend);

        for(Attendance a : attendances) {
            Event e = eventRepository.findById(a.getEventId()).orElse(null);
            if(e != null) {
                if(e.getType().equals(EventType.TRAINING)){
                    traingCnt++;
                }else if(e.getType().equals(EventType.COMPETITION)){
                    compCnt++;
                }
            }
        }

        user.editUserCnt(traingCnt,compCnt);

        userRepository.save(user);
    }
}
