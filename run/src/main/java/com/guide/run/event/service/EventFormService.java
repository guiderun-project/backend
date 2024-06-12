package com.guide.run.event.service;

import com.guide.run.event.entity.Event;
import com.guide.run.event.entity.EventForm;

import com.guide.run.event.entity.dto.request.form.CreateEventForm;
import com.guide.run.event.entity.dto.response.form.GetForm;
import com.guide.run.event.entity.repository.EventFormRepository;
import com.guide.run.event.entity.repository.EventRepository;
import com.guide.run.global.exception.event.logic.ExistFormException;
import com.guide.run.global.exception.event.logic.NotValidDurationException;
import com.guide.run.global.exception.event.resource.NotExistEventException;
import com.guide.run.global.exception.user.resource.NotExistUserException;
import com.guide.run.temp.member.entity.Attendance;
import com.guide.run.temp.member.repository.AttendanceRepository;
import com.guide.run.user.entity.user.User;
import com.guide.run.user.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.guide.run.event.entity.type.EventRecruitStatus.RECRUIT_OPEN;

@Service
@RequiredArgsConstructor
public class EventFormService {
    private final EventFormRepository eventFormRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final AttendanceRepository attendanceRepository;

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
        EventForm form = eventFormRepository.findByEventIdAndPrivateId(eventId, userId);
        User user = userRepository.findUserByPrivateId(userId).orElseThrow(NotExistUserException::new);
        return GetForm.builder()
                .type(user.getType().getValue())
                .name(user.getName())
                .pace(user.getRecordDegree())
                .group(form.getHopeTeam())
                .partner(form.getHopePartner())
                .detail(form.getReferContent())
                .build();
    }
}
