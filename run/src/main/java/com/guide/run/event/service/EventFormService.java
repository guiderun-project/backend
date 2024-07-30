package com.guide.run.event.service;

import com.guide.run.event.entity.Event;
import com.guide.run.event.entity.EventForm;

import com.guide.run.event.entity.dto.request.form.CreateEventForm;
import com.guide.run.event.entity.dto.response.form.GetAllForms;
import com.guide.run.event.entity.dto.response.form.GetForm;
import com.guide.run.event.entity.repository.EventFormRepository;
import com.guide.run.event.entity.repository.EventRepository;
import com.guide.run.global.exception.event.logic.ExistFormException;
import com.guide.run.global.exception.event.logic.NotValidDurationException;
import com.guide.run.global.exception.event.resource.NotExistEventException;
import com.guide.run.global.exception.user.resource.NotExistUserException;
import com.guide.run.partner.entity.matching.Matching;
import com.guide.run.partner.entity.matching.UnMatching;
import com.guide.run.partner.entity.matching.repository.MatchingRepository;
import com.guide.run.partner.entity.matching.repository.UnMatchingRepository;
import com.guide.run.temp.member.entity.Attendance;
import com.guide.run.temp.member.repository.AttendanceRepository;
import com.guide.run.user.entity.type.UserType;
import com.guide.run.user.entity.user.User;
import com.guide.run.user.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.guide.run.event.entity.type.EventRecruitStatus.RECRUIT_OPEN;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventFormService {
    private final EventFormRepository eventFormRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final AttendanceRepository attendanceRepository;
    private final UnMatchingRepository unMatchingRepository;
    private final MatchingRepository matchingRepository;

    @Transactional
    public Long createForm(CreateEventForm createForm, Long eventId, String userId) {
        Event event = eventRepository.findById(eventId).orElseThrow(NotExistEventException::new);
        if(!event.getRecruitStatus().equals(RECRUIT_OPEN))
            throw new NotValidDurationException();
        User user = userRepository.findUserByPrivateId(userId).orElseThrow(NotExistUserException::new);
        List<EventForm> forms = eventFormRepository.findAllByEventIdAndPrivateId(eventId, userId);
        if(forms.size()>0)
            throw new ExistFormException();
        attendanceRepository.save(
                Attendance.builder()
                        .eventId(eventId)
                        .privateId(user.getPrivateId())
                        .isAttend(false)
                        .build()
        );
        unMatchingRepository.save(
                UnMatching.builder().eventId(eventId).privateId(user.getPrivateId()).build()
        );
        //if(user.getType().equals(UserType.GUIDE)){
         //   event.setGuideCnt(event.getGuideCnt()+1);
        //}
        //else {
         //   event.setViCnt(event.getViCnt()+1);
        //}

        return eventFormRepository.save(
                EventForm.builder()
                        .privateId(userId)
                        .eventId(eventId)
                        .type(user.getType())
                        .age(user.getAge())
                        .gender(user.getGender())
                        .hopePartner(createForm.getPartner())
                        .hopeTeam(createForm.getGroup())
                        .referContent(createForm.getDetail())
                        .isMatching(false)
                        .build()
        ).getId();
    }

    @Transactional
    public Long patchForm(CreateEventForm createForm, Long eventId, String userId) {
        Event event = eventRepository.findById(eventId).orElseThrow(NotExistEventException::new);
        if(!event.getRecruitStatus().equals(RECRUIT_OPEN))
            throw new NotValidDurationException();
        User user = userRepository.findUserByPrivateId(userId).orElseThrow(NotExistUserException::new);
        EventForm form = eventFormRepository.findByEventIdAndPrivateId(eventId, userId);
        form.setform(createForm.getGroup(), createForm.getPartner(), createForm.getDetail());
        return eventFormRepository.save(
                form
        ).getId();
    }

    public GetForm getForm(Long eventId, String userId) {
        Event event = eventRepository.findById(eventId).orElseThrow(NotExistEventException::new);
        User user = userRepository.findUserByUserId(userId).orElseThrow(NotExistUserException::new);
        EventForm form = eventFormRepository.findByEventIdAndPrivateId(eventId, user.getPrivateId());
        return GetForm.builder()
                .type(user.getType().getValue())
                .name(user.getName())
                .pace(user.getRecordDegree())
                .group(form.getHopeTeam())
                .partner(form.getHopePartner())
                .detail(form.getReferContent())
                .build();
    }

    public GetAllForms getAllForms(Long eventId) {
        return GetAllForms.builder()
                .vi(eventFormRepository.findAllEventIdAndUserType(eventId, UserType.VI))
                .guide(eventFormRepository.findAllEventIdAndUserType(eventId,UserType.GUIDE))
                .build();
    }

    @Transactional
    public void deleteForm(Long eventId, String privateId) {
        Event event = eventRepository.findById(eventId).orElseThrow(NotExistEventException::new);
        User user = userRepository.findUserByPrivateId(privateId).orElseThrow(NotExistUserException::new);
        EventForm form = eventFormRepository.findByEventIdAndPrivateId(eventId, privateId);
        eventFormRepository.delete(form);
        //if(user.getType().equals(UserType.GUIDE)){
        //    event.setGuideCnt(event.getGuideCnt()-1);
        //}
        //else {
        //    event.setViCnt(event.getViCnt()-1);
        //
        //create 당시 생긴 모든거 삭제 매칭도 뒤지고
        //출석 제거
        Attendance attendance = attendanceRepository.findByEventIdAndPrivateId(eventId, privateId);
        log.info("eventId : "+event+", privateId : "+privateId);
        attendanceRepository.delete(attendance);
        //매칭 or 미매칭 제거
        Optional<UnMatching> unMatching = unMatchingRepository.findByPrivateIdAndEventId(privateId, eventId);
        if(unMatching.isEmpty()){
            Optional<Matching> matching = matchingRepository.findByEventIdAndViId(eventId,privateId);
            matchingRepository.delete(matching.get());
        }else{
            unMatchingRepository.delete(unMatching.get());
        }
    }
}
