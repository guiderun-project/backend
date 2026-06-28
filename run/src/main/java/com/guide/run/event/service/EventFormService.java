package com.guide.run.event.service;

import com.guide.run.event.entity.Event;
import com.guide.run.event.entity.EventForm;

import com.guide.run.event.entity.dto.request.EventApplyRequest;
import com.guide.run.event.entity.dto.response.form.EventApplicantFormResponse;
import com.guide.run.event.entity.dto.response.form.EventApplicantListResponse;
import com.guide.run.event.entity.dto.response.form.EventCanceledApplicantListResponse;
import com.guide.run.event.entity.dto.response.form.GetAllForms;
import com.guide.run.event.entity.dto.response.form.MyEventApplyGetResponse;
import com.guide.run.event.entity.repository.EventFormRepository;
import com.guide.run.event.entity.repository.EventRepository;
import com.guide.run.event.entity.type.EventFormStatus;
import com.guide.run.event.entity.type.EventType;
import com.guide.run.global.exception.event.authorize.NotEventOrganizerException;
import com.guide.run.global.exception.event.logic.EventValidationException;
import com.guide.run.global.exception.event.logic.ExistFormException;
import com.guide.run.global.exception.event.logic.NotValidDurationException;
import com.guide.run.global.exception.event.resource.NotExistEventException;
import com.guide.run.global.exception.event.resource.NotExistFormException;
import com.guide.run.global.exception.user.resource.NotExistUserException;
import com.guide.run.partner.entity.matching.Matching;
import com.guide.run.partner.entity.matching.UnMatching;
import com.guide.run.partner.entity.matching.repository.MatchingRepository;
import com.guide.run.partner.entity.matching.repository.UnMatchingRepository;
import com.guide.run.attendance.entity.Attendance;
import com.guide.run.attendance.repository.AttendanceRepository;
import com.guide.run.user.entity.type.Role;
import com.guide.run.user.entity.type.UserType;
import com.guide.run.user.entity.user.User;
import com.guide.run.user.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
    private final EventAdditionalInfoService eventAdditionalInfoService;

    @Transactional
    public Long createForm(EventApplyRequest createForm, Long eventId, String userId) {
        Event event = eventRepository.findById(eventId).orElseThrow(NotExistEventException::new);
        if(!EventTemporalStatusResolver.resolveRecruitStatus(event).equals(RECRUIT_OPEN))
            throw new NotValidDurationException();
        User user = userRepository.findUserByPrivateId(userId).orElseThrow(NotExistUserException::new);
        EventForm appliedForm = eventFormRepository.findByEventIdAndPrivateIdAndStatus(
                eventId,
                userId,
                EventFormStatus.APPLIED
        );
        if(appliedForm != null)
            throw new ExistFormException();
        validateCompetitionInfo(event, createForm);
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

        EventForm savedForm = eventFormRepository.save(
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
                        .eventCategory(event.getEventCategory())
                        .runningDistanceKm(event.getExpectedRunningDistanceKm())
                        .birthDate(getBirthDate(createForm))
                        .phoneNumber(getPhoneNumber(createForm))
                        .status(EventFormStatus.APPLIED)
                        .build()
        );
        eventAdditionalInfoService.replaceAnswers(eventId, savedForm.getId(), createForm.getAdditionalAnswers());
        return savedForm.getId();
    }

    @Transactional
    public Long patchForm(EventApplyRequest createForm, Long eventId, String userId) {
        Event event = eventRepository.findById(eventId).orElseThrow(NotExistEventException::new);
        if(!EventTemporalStatusResolver.resolveRecruitStatus(event).equals(RECRUIT_OPEN))
            throw new NotValidDurationException();
        userRepository.findUserByPrivateId(userId).orElseThrow(NotExistUserException::new);
        EventForm form = eventFormRepository.findByEventIdAndPrivateIdAndStatus(
                eventId,
                userId,
                EventFormStatus.APPLIED
        );
        if (form == null) {
            throw new NotExistFormException("해당 이벤트에 대한 신청 폼이 존재하지 않습니다.");
        }
        validateCompetitionInfo(event, createForm);
        form.setform(createForm.getGroup(), createForm.getPartner(), createForm.getDetail(),event.getEventCategory());
        form.updateCompetitionInfo(getBirthDate(createForm), getPhoneNumber(createForm));
        EventForm savedForm = eventFormRepository.save(form);
        eventAdditionalInfoService.replaceAnswers(eventId, savedForm.getId(), createForm.getAdditionalAnswers());
        return savedForm.getId();
    }

    public MyEventApplyGetResponse getMyForm(Long eventId, String privateId) {
        Event event = eventRepository.findById(eventId).orElseThrow(NotExistEventException::new);
        User user = userRepository.findUserByPrivateId(privateId).orElseThrow(NotExistUserException::new);
        EventForm form = eventFormRepository.findByEventIdAndPrivateIdAndStatus(
                eventId,
                privateId,
                EventFormStatus.APPLIED
        );
        if (form == null) {
            throw new NotExistFormException("해당 이벤트에 대한 신청 폼이 존재하지 않습니다.");
        }

        return MyEventApplyGetResponse.builder()
                .eventId(event.getId())
                .eventName(event.getName())
                .eventType(event.getType())
                .eventCategory(event.getEventCategory())
                .userType(user.getType())
                .name(user.getName())
                .recordDegree(user.getRecordDegree())
                .applicationInfo(MyEventApplyGetResponse.ApplicationInfo.builder()
                        .group(form.getHopeTeam())
                        .partner(form.getHopePartner())
                        .detail(form.getReferContent())
                        .build())
                .competitionInfo(toCompetitionInfo(form))
                .additionalAnswers(eventAdditionalInfoService.getAnswerDetails(form.getId()))
                .build();
    }

    public EventApplicantListResponse getApplicantForms(Long eventId) {
        List<EventForm> forms = eventFormRepository.findAllByEventIdAndStatus(eventId, EventFormStatus.APPLIED);
        long viCount = forms.stream()
                .filter(form -> form.getType() == UserType.VI)
                .count();
        long guideCount = forms.stream()
                .filter(form -> form.getType() == UserType.GUIDE)
                .count();

        Map<String, List<EventForm>> groupedForms = new LinkedHashMap<>();
        for (EventForm form : forms) {
            groupedForms.computeIfAbsent(form.getHopeTeam(), key -> new ArrayList<>()).add(form);
        }

        List<EventApplicantListResponse.EventApplicantGroup> groups = groupedForms.entrySet().stream()
                .map(entry -> EventApplicantListResponse.EventApplicantGroup.builder()
                        .runningGroup(entry.getKey())
                        .totalCount(entry.getValue().size())
                        .applicants(entry.getValue().stream()
                                .map(this::toApplicant)
                                .toList())
                        .build())
                .toList();

        return EventApplicantListResponse.builder()
                .summary(EventApplicantListResponse.Summary.builder()
                        .totalCount(forms.size())
                        .viCount(viCount)
                        .guideCount(guideCount)
                        .build())
                .groups(groups)
                .build();
    }

    public EventApplicantFormResponse getApplicantForm(Long eventId, String userId, String requesterPrivateId) {
        Event event = eventRepository.findById(eventId).orElseThrow(NotExistEventException::new);
        User requester = userRepository.findUserByPrivateId(requesterPrivateId).orElseThrow(NotExistUserException::new);
        if (!event.getOrganizer().equals(requesterPrivateId) && !Role.ROLE_ADMIN.equals(requester.getRole())) {
            throw new NotEventOrganizerException();
        }

        User applicant = userRepository.findUserByUserId(userId).orElseThrow(NotExistUserException::new);
        EventForm form = eventFormRepository.findByEventIdAndPrivateIdAndStatus(
                eventId,
                applicant.getPrivateId(),
                EventFormStatus.APPLIED
        );
        if (form == null) {
            throw new NotExistFormException("해당 이벤트에 대한 신청 폼이 존재하지 않습니다.");
        }

        return EventApplicantFormResponse.builder()
                .applicant(EventApplicantFormResponse.Applicant.builder()
                        .userId(applicant.getUserId())
                        .name(applicant.getName())
                        .type(applicant.getType())
                        .birthDate(form.getBirthDate())
                        .phoneNumber(form.getPhoneNumber())
                        .build())
                .form(EventApplicantFormResponse.Form.builder()
                        .applyGroup(form.getHopeTeam())
                        .hopePartner(form.getHopePartner())
                        .additionalComment(form.getReferContent())
                        .build())
                .additionalAnswers(toApplicantAdditionalAnswers(form.getId()))
                .build();
    }

    public EventCanceledApplicantListResponse getCanceledApplicantForms(Long eventId) {
        eventRepository.findById(eventId).orElseThrow(NotExistEventException::new);
        List<EventForm> canceledForms = eventFormRepository.findAllByEventIdAndStatus(eventId, EventFormStatus.CANCELED);

        List<EventCanceledApplicantListResponse.CanceledApplicant> applicants = canceledForms.stream()
                .map(form -> {
                    User user = userRepository.findUserByPrivateId(form.getPrivateId()).orElse(null);
                    if (user == null) return null;
                    return EventCanceledApplicantListResponse.CanceledApplicant.builder()
                            .userId(user.getUserId())
                            .name(user.getName())
                            .type(user.getType())
                            .canceledAt(form.getCanceledAt())
                            .build();
                })
                .filter(a -> a != null)
                .toList();

        long viCount = applicants.stream().filter(a -> a.getType() == UserType.VI).count();
        long guideCount = applicants.stream().filter(a -> a.getType() == UserType.GUIDE).count();

        return EventCanceledApplicantListResponse.builder()
                .summary(EventCanceledApplicantListResponse.Summary.builder()
                        .totalCount(applicants.size())
                        .viCount(viCount)
                        .guideCount(guideCount)
                        .build())
                .canceledApplicants(applicants)
                .build();
    }

    public GetAllForms getAllForms(Long eventId, String privateId) {
        User user = userRepository.findUserByPrivateId(privateId).orElseThrow(NotExistUserException::new);
        if(user.getRole().equals(Role.ROLE_ADMIN)){
            return (GetAllForms.builder()
                    .vi(eventFormRepository.findAllFormsWithPhone(eventId, UserType.VI, EventFormStatus.APPLIED))
                    .guide(eventFormRepository.findAllFormsWithPhone(eventId,UserType.GUIDE, EventFormStatus.APPLIED))
                    .build());
        }else{
            return (GetAllForms.builder()
                    .vi(eventFormRepository.findAllFormsWithoutPhone(eventId, UserType.VI, EventFormStatus.APPLIED))
                    .guide(eventFormRepository.findAllFormsWithoutPhone(eventId,UserType.GUIDE, EventFormStatus.APPLIED))
                    .build());
        }
    }

    @Transactional
    public void deleteForm(Long eventId, String privateId) {
        Event event = eventRepository.findById(eventId).orElseThrow(NotExistEventException::new);
        User user = userRepository.findUserByPrivateId(privateId).orElseThrow(NotExistUserException::new);
        EventForm form = eventFormRepository.findByEventIdAndPrivateIdAndStatus(
                eventId,
                privateId,
                EventFormStatus.APPLIED
        );
        if (form == null) {
            throw new NotExistFormException("해당 이벤트에 대한 신청 폼이 존재하지 않습니다.");
        }
        form.cancel(LocalDateTime.now());
        eventFormRepository.save(form);
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
            //guide가 취소한 경우 vi와 연결된 guide가 더이상 없는 경우 unmatching보내줘야함
            if(user.getType()==UserType.GUIDE) {
                Matching m = matchingRepository.findByEventIdAndGuideId(eventId, privateId);
                matchingRepository.delete(m);
                matchingRepository.flush();

                if(matchingRepository.findAllByEventIdAndViId(eventId,m.getViId()).size()==0){
                    unMatchingRepository.save(
                            UnMatching.builder()
                                    .eventId(eventId)
                                    .privateId(m.getViId())
                                    .build()
                    );
                }
            }
            //vi가 취소한 경우 연결된 guide들 unmatching으로 넣어줘야함
            else{
                List<Matching> matchingByEventIdAndViId = matchingRepository.findAllByEventIdAndViId(eventId, privateId);
                for(Matching m : matchingByEventIdAndViId){
                    matchingRepository.delete(m);
                    unMatchingRepository.save(
                            UnMatching.builder()
                                    .privateId(m.getGuideId())
                                    .eventId(eventId)
                                    .build()
                    );
                }
            }
        }else{
            unMatchingRepository.delete(unMatching.get());
        }
    }

    private void validateCompetitionInfo(Event event, EventApplyRequest request) {
        if (event.getType() != EventType.COMPETITION) {
            return;
        }

        EventApplyRequest.CompetitionApplicationInfo competitionInfo = request.getCompetitionInfo();
        if (competitionInfo == null
                || competitionInfo.getBirthDate() == null
                || competitionInfo.getPhoneNumber() == null
                || competitionInfo.getPhoneNumber().isBlank()) {
            throw new EventValidationException("대회 신청 정보는 필수입니다.");
        }
    }

    private LocalDate getBirthDate(EventApplyRequest request) {
        if (request.getCompetitionInfo() == null) {
            return null;
        }
        return request.getCompetitionInfo().getBirthDate();
    }

    private String getPhoneNumber(EventApplyRequest request) {
        if (request.getCompetitionInfo() == null) {
            return null;
        }
        return request.getCompetitionInfo().getPhoneNumber();
    }

    private EventApplyRequest.CompetitionApplicationInfo toCompetitionInfo(EventForm form) {
        if (form.getBirthDate() == null && form.getPhoneNumber() == null) {
            return null;
        }

        return new EventApplyRequest.CompetitionApplicationInfo(
                form.getBirthDate(),
                form.getPhoneNumber()
        );
    }

    private EventApplicantListResponse.EventApplicant toApplicant(EventForm form) {
        User user = userRepository.findUserByPrivateId(form.getPrivateId()).orElseThrow(NotExistUserException::new);
        return EventApplicantListResponse.EventApplicant.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .type(user.getType())
                .isFirstParticipation(user.getTrainingCnt() + user.getCompetitionCnt() == 0)
                .build();
    }

    private List<EventApplicantFormResponse.AdditionalAnswer> toApplicantAdditionalAnswers(Long formId) {
        return eventAdditionalInfoService.getAnswerDetails(formId).stream()
                .map(answer -> EventApplicantFormResponse.AdditionalAnswer.builder()
                        .questionId(answer.getQuestionId())
                        .questionTitle(answer.getQuestion())
                        .questionType(answer.getType())
                        .answer(answer.getAnswerText() != null ? answer.getAnswerText() : answer.getSelectedOptionValue())
                        .build())
                .toList();
    }
}
