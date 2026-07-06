package com.guide.run.event.service;

import com.guide.run.attendance.entity.Attendance;
import com.guide.run.attendance.repository.AttendanceRepository;
import com.guide.run.event.entity.EventForm;
import com.guide.run.event.entity.dto.response.form.Form;
import com.guide.run.event.entity.dto.request.match.MatchingCreateRequest;
import com.guide.run.event.entity.dto.response.match.*;
import com.guide.run.event.entity.repository.EventFormRepository;
import com.guide.run.event.entity.repository.EventRepository;
import com.guide.run.event.entity.type.EventFormStatus;
import com.guide.run.global.exception.event.resource.NotExistEventException;
import com.guide.run.global.exception.event.resource.NotExistFormException;
import com.guide.run.global.exception.user.resource.NotExistUserException;
import com.guide.run.partner.entity.matching.Matching;
import com.guide.run.partner.entity.matching.UnMatching;
import com.guide.run.partner.entity.matching.repository.MatchingRepository;
import com.guide.run.partner.entity.matching.repository.UnMatchingRepository;
import com.guide.run.partner.service.PartnerService;
import com.guide.run.user.entity.type.UserType;
import com.guide.run.user.entity.user.User;
import com.guide.run.user.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventMatchingService {
    private static final Comparator<String> TEXT_ORDER = Comparator.nullsLast(String::compareTo);
    private static final Comparator<MatchingStatusRow> MATCHING_STATUS_ROW_ORDER = Comparator
            .comparingInt(EventMatchingService::matchingStatusRowUserTypeOrder)
            .thenComparing(EventMatchingService::matchingStatusRowName, TEXT_ORDER)
            .thenComparing(EventMatchingService::matchingStatusRowUserId, TEXT_ORDER);
    private static final Comparator<MatchingWaitingParticipant> MATCHING_WAITING_PARTICIPANT_ORDER = Comparator
            .comparingInt((MatchingWaitingParticipant participant) -> userTypeOrder(participant.getType()))
            .thenComparing(MatchingWaitingParticipant::getName, TEXT_ORDER)
            .thenComparing(MatchingWaitingParticipant::getUserId, TEXT_ORDER);

    private final UnMatchingRepository unMatchingRepository;
    private final MatchingRepository matchingRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final EventFormRepository eventFormRepository;
    private final PartnerService partnerService;
    private final AttendanceRepository attendanceRepository;

    @Transactional
    public MatchingCreateResponse createMatching(Long eventId, MatchingCreateRequest request) {
        eventRepository.findById(eventId).orElseThrow(NotExistEventException::new);

        for (String guideId : request.getGuideIds()) {
            matchUser(eventId, request.getViId(), guideId);
        }

        int waitingCount = (int) (unMatchingRepository.getUserTypeCount(eventId, UserType.VI)
                + unMatchingRepository.getUserTypeCount(eventId, UserType.GUIDE));
        int completedViCount = matchingRepository.findAllMatchedViByEventIdAndUserType(eventId, UserType.VI).size();
        int matchedGuideCount = matchingRepository.findAllByEventId(eventId).size();

        return MatchingCreateResponse.builder()
                .viId(request.getViId())
                .guideIds(request.getGuideIds())
                .summary(MatchingCreateResponse.Summary.builder()
                        .waitingCount(waitingCount)
                        .completedViCount(completedViCount)
                        .matchedGuideCount(matchedGuideCount)
                        .build())
                .build();
    }

    @Transactional
    public void matchUser(Long eventId, String viId, String userId) {
        eventRepository.findById(eventId).orElseThrow(NotExistEventException::new);
        User vi = userRepository.findUserByUserId(viId).orElseThrow(NotExistUserException::new);
        User guide = userRepository.findUserByUserId(userId).orElseThrow(NotExistUserException::new);
        EventForm viForm = eventFormRepository.findByEventIdAndPrivateIdAndStatus(
                eventId,
                vi.getPrivateId(),
                EventFormStatus.APPLIED
        );
        EventForm guideForm = eventFormRepository.findByEventIdAndPrivateIdAndStatus(
                eventId,
                guide.getPrivateId(),
                EventFormStatus.APPLIED
        );
        if (viForm == null || guideForm == null) {
            throw new NotExistFormException("해당 이벤트에 대한 신청 폼이 존재하지 않습니다.");
        }
        Matching existGuide = matchingRepository.findByEventIdAndGuideId(eventId, guide.getPrivateId());
        if(existGuide!=null){
            //existGuide의 파트너 삭제 추가
            partnerService.setNotAttendGuidePartner(eventId, guide);

            matchingRepository.delete(existGuide);
            long countMatchedGuideForVi = matchingRepository.countByEventIdAndViId(eventId, existGuide.getViId());
            if(countMatchedGuideForVi==0){
                User unMatchingVi = userRepository.findUserByPrivateId(existGuide.getViId()).orElseThrow(NotExistUserException::new);
                unMatchingRepository.save(
                        UnMatching.builder()
                                .eventId(eventId)
                                .privateId(unMatchingVi.getPrivateId())
                                .build()
                );

                //unMatching vi의 파트너 삭제 추가
                partnerService.setNotAttendViPartnerList(eventId, unMatchingVi);

            }


        }


        matchingRepository.save(
                Matching.builder()
                        .eventId(eventId)
                        .guideId(guide.getPrivateId())
                        .viId(vi.getPrivateId())
                        .viRecord(viForm.getHopeTeam())
                        .guideRecord(guideForm.getHopeTeam())
                        .build()
        );
        unMatchingRepository.delete(
                UnMatching.builder()
                        .eventId(eventId)
                        .privateId(guide.getPrivateId())
                        .build()
        );
        Optional<UnMatching> findVi = unMatchingRepository.findByPrivateIdAndEventId(vi.getPrivateId(),eventId);

        if(!findVi.isEmpty()){
            unMatchingRepository.delete(findVi.get());
        }

        //출석 되어 있으면 파트너 반영.
        updatePartnerOnAttendance(eventId, guide);


    }

    @Transactional
    public MatchingCancelResponse cancelMatching(Long eventId, String viId) {
        eventRepository.findById(eventId).orElseThrow(NotExistEventException::new);
        User vi = userRepository.findUserByUserId(viId).orElseThrow(NotExistUserException::new);
        String viPrivateId = vi.getPrivateId();

        List<Matching> allMatching = matchingRepository.findAllByEventIdAndViId(eventId, viPrivateId);
        partnerService.setNotAttendViPartnerList(eventId, vi);

        List<String> canceledGuideIds = new ArrayList<>();
        for (Matching m : allMatching) {
            User guide = userRepository.findUserByPrivateId(m.getGuideId()).orElseThrow(NotExistUserException::new);
            canceledGuideIds.add(guide.getUserId());
            matchingRepository.delete(m);
            unMatchingRepository.save(UnMatching.builder()
                    .privateId(m.getGuideId())
                    .eventId(eventId)
                    .build());
        }

        unMatchingRepository.save(UnMatching.builder()
                .privateId(viPrivateId)
                .eventId(eventId)
                .build());

        int waitingCount = (int) (unMatchingRepository.getUserTypeCount(eventId, UserType.VI)
                + unMatchingRepository.getUserTypeCount(eventId, UserType.GUIDE));
        int completedViCount = matchingRepository.findAllMatchedViByEventIdAndUserType(eventId, UserType.VI).size();
        int matchedGuideCount = matchingRepository.findAllByEventId(eventId).size();

        return MatchingCancelResponse.builder()
                .viId(viId)
                .canceledGuideIds(canceledGuideIds)
                .summary(MatchingCancelResponse.Summary.builder()
                        .waitingCount(waitingCount)
                        .completedViCount(completedViCount)
                        .matchedGuideCount(matchedGuideCount)
                        .build())
                .build();
    }

    @Transactional
    public void deleteMatchUser(Long eventId, String userId) {
        eventRepository.findById(eventId).orElseThrow(NotExistEventException::new);
        User user = userRepository.findUserByUserId(userId).orElseThrow(NotExistUserException::new);
        String privateId = user.getPrivateId();

        if(user.getType()==UserType.VI){
            List<Matching> allMatching = matchingRepository.findAllByEventIdAndViId(eventId, privateId);
            //vi 파트너 삭제 추가
            partnerService.setNotAttendViPartnerList(eventId, user);

            for(Matching m : allMatching){
                matchingRepository.delete(m);
                unMatchingRepository.save(
                        UnMatching.builder()
                                .privateId(m.getGuideId())
                                .eventId(eventId)
                                .build()
                );
            }

            unMatchingRepository.save(
                    UnMatching.builder()
                            .privateId(user.getPrivateId())
                            .eventId(eventId)
                            .build()
            );



        }else{
            Matching m = matchingRepository.findByEventIdAndGuideId(eventId, privateId);

            //파트너 삭제 추가
            partnerService.setNotAttendGuidePartner(eventId, user);

            matchingRepository.delete(m);
            unMatchingRepository.save(
                    UnMatching.builder()
                            .privateId(m.getGuideId())
                            .eventId(eventId)
                            .build()
            );
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
    }

    public UserTypeCount getUserTypeCount(Long eventId) {
        eventRepository.findById(eventId).orElseThrow(NotExistEventException::new);
        return UserTypeCount.builder()
                .vi(unMatchingRepository.getUserTypeCount(eventId, UserType.VI))
                .guide(unMatchingRepository.getUserTypeCount(eventId,UserType.GUIDE))
                .build();
    }

    public List<NotMatchUserInfo> getNotMatchList(Long eventId) {
        eventRepository.findById(eventId).orElseThrow(NotExistEventException::new);
        return unMatchingRepository.findNotMatchUserInfos(eventId);
    }

    public MatchedGuideCount getMatchedGuideCount(Long eventId, String viId) {
        eventRepository.findById(eventId).orElseThrow(NotExistEventException::new);
        User vi = userRepository.findUserByUserId(viId).orElseThrow(NotExistUserException::new);
        return MatchedGuideCount.builder()
                .guide(matchingRepository.findAllByEventIdAndViId(eventId,vi.getPrivateId()).size()).build();
    }

    public MatchedGuideList getMatchedGuideList(Long eventId, String viId) {
        eventRepository.findById(eventId).orElseThrow(NotExistEventException::new);
        User vi = userRepository.findUserByUserId(viId).orElseThrow(NotExistUserException::new);
        return MatchedGuideList.builder()
                .guide(matchingRepository.findAllMatchedGuideByEventIdAndViId(eventId,vi.getPrivateId()))
                .build();
    }

    public MatchedViCount getMatchedViCount(Long eventId) {
        eventRepository.findById(eventId).orElseThrow(NotExistEventException::new);
        return MatchedViCount.builder()
                .vi(matchingRepository.findAllMatchedViByEventIdAndUserType(eventId,UserType.VI).size())
                .build();
    }

    public MatchedViList getMatchedViList(Long eventId) {
        eventRepository.findById(eventId).orElseThrow(NotExistEventException::new);
        return MatchedViList.builder()
                .vi(matchingRepository.findAllMatchedViByEventIdAndUserType(eventId,UserType.VI))
                .build();
    }

    public EventMatchingStatusResponse getMatchingStatus(Long eventId, String loginPrivateId) {
        eventRepository.findById(eventId).orElseThrow(NotExistEventException::new);
        User loginUser = userRepository.findUserByPrivateId(loginPrivateId).orElseThrow(NotExistUserException::new);

        // 1. myPartners
        List<MatchingStatusUser> myPartners = buildMyPartners(eventId, loginUser);

        // 2. 매칭된 VI+Guide 쌍 → VI의 hopeTeam 기준 그룹핑
        List<MatchingCompletedFlatDto> matchedFlat = matchingRepository.findMatchingCompletedByEventId(eventId);

        // (runningGroup → (viUserId → MatchingStatusRow))
        LinkedHashMap<String, LinkedHashMap<String, MatchingStatusRow>> groupRowMap = new LinkedHashMap<>();
        for (MatchingCompletedFlatDto flat : matchedFlat) {
            String rg = flat.getViRunningGroup() != null ? flat.getViRunningGroup() : "";
            groupRowMap.computeIfAbsent(rg, k -> new LinkedHashMap<>());
            LinkedHashMap<String, MatchingStatusRow> rowMap = groupRowMap.get(rg);

            if (!rowMap.containsKey(flat.getViUserId())) {
                MatchingStatusUser vi = MatchingStatusUser.builder()
                        .userId(flat.getViUserId())
                        .name(flat.getViName())
                        .type(flat.getViType())
                        .applyGroup(flat.getViRunningGroup())
                        .build();
                rowMap.put(flat.getViUserId(), MatchingStatusRow.builder()
                        .vi(vi)
                        .guides(new ArrayList<>())
                        .build());
            }
            MatchingStatusUser guide = MatchingStatusUser.builder()
                    .userId(flat.getGuideUserId())
                    .name(flat.getGuideName())
                    .type(flat.getGuideType())
                    .applyGroup(flat.getGuideApplyRecord())
                    .build();
            rowMap.get(flat.getViUserId()).getGuides().add(guide);
        }

        // 3. 미매칭 참가자 → VI는 독립 행, Guide는 vi=null 행으로 추가
        List<MatchingWaitingFlatDto> waitingFlat = unMatchingRepository.findWaitingParticipants(eventId);
        for (MatchingWaitingFlatDto flat : waitingFlat) {
            String rg = flat.getHopeTeam() != null ? flat.getHopeTeam() : "";
            groupRowMap.computeIfAbsent(rg, k -> new LinkedHashMap<>());
            if (flat.getType() == UserType.VI) {
                MatchingStatusUser viUser = MatchingStatusUser.builder()
                        .userId(flat.getUserId())
                        .name(flat.getName())
                        .type(flat.getType())
                        .applyGroup(flat.getHopeTeam())
                        .build();
                groupRowMap.get(rg).put("unmatched_vi_" + flat.getUserId(), MatchingStatusRow.builder()
                        .vi(viUser)
                        .guides(Collections.emptyList())
                        .build());
                continue;
            }
            if (flat.getType() != UserType.GUIDE) continue;
            MatchingStatusUser guideUser = MatchingStatusUser.builder()
                    .userId(flat.getUserId())
                    .name(flat.getName())
                    .type(flat.getType())
                    .applyGroup(flat.getHopeTeam())
                    .build();
            groupRowMap.get(rg).put("unmatched_" + flat.getUserId(), MatchingStatusRow.builder()
                    .vi(null)
                    .guides(Collections.singletonList(guideUser))
                    .build());
        }

        // 4. groups 빌드 (runningGroup 오름차순 정렬)
        List<MatchingStatusGroup> groups = groupRowMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> {
                    List<MatchingStatusRow> rows = e.getValue().values().stream()
                            .sorted(MATCHING_STATUS_ROW_ORDER)
                            .collect(Collectors.toList());
                    return MatchingStatusGroup.builder()
                            .runningGroup(e.getKey())
                            .totalCount(rows.size())
                            .rows(rows)
                            .build();
                })
                .collect(Collectors.toList());

        return EventMatchingStatusResponse.builder()
                .myPartners(myPartners)
                .groups(groups)
                .build();
    }

    private List<MatchingStatusUser> buildMyPartners(Long eventId, User loginUser) {
        List<MatchingStatusUser> partners = new ArrayList<>();
        if (loginUser.getType() == UserType.VI) {
            List<Matching> matchings = matchingRepository.findAllByEventIdAndViId(eventId, loginUser.getPrivateId());
            for (Matching m : matchings) {
                User guide = userRepository.findUserByPrivateId(m.getGuideId()).orElse(null);
                if (guide == null) continue;
                EventForm form = eventFormRepository.findByEventIdAndPrivateIdAndStatus(
                        eventId,
                        guide.getPrivateId(),
                        EventFormStatus.APPLIED
                );
                partners.add(MatchingStatusUser.builder()
                        .userId(guide.getUserId())
                        .name(guide.getName())
                        .type(guide.getType())
                        .applyGroup(form != null ? form.getHopeTeam() : null)
                        .build());
            }
        } else if (loginUser.getType() == UserType.GUIDE) {
            Matching matching = matchingRepository.findByEventIdAndGuideId(eventId, loginUser.getPrivateId());
            if (matching != null) {
                User vi = userRepository.findUserByPrivateId(matching.getViId()).orElse(null);
                if (vi != null) {
                    EventForm form = eventFormRepository.findByEventIdAndPrivateIdAndStatus(
                            eventId,
                            vi.getPrivateId(),
                            EventFormStatus.APPLIED
                    );
                    partners.add(MatchingStatusUser.builder()
                            .userId(vi.getUserId())
                            .name(vi.getName())
                            .type(vi.getType())
                            .applyGroup(form != null ? form.getHopeTeam() : null)
                            .build());
                }
            }
        }
        return partners;
    }

    public MatchingWaitingResponse getMatchingWaiting(Long eventId) {
        eventRepository.findById(eventId).orElseThrow(NotExistEventException::new);

        List<MatchingWaitingFlatDto> flatList = unMatchingRepository.findWaitingParticipants(eventId);

        int viCount = (int) flatList.stream().filter(f -> f.getType() == UserType.VI).count();
        int guideCount = (int) flatList.stream().filter(f -> f.getType() == UserType.GUIDE).count();

        LinkedHashMap<String, List<MatchingWaitingParticipant>> groupMap = new LinkedHashMap<>();
        for (MatchingWaitingFlatDto flat : flatList) {
            String runningGroup = flat.getHopeTeam() != null ? flat.getHopeTeam() : "";
            MatchingWaitingParticipant participant = MatchingWaitingParticipant.builder()
                    .userId(flat.getUserId())
                    .name(flat.getName())
                    .type(flat.getType())
                    .originalRunningGroup(flat.getHopeTeam())
                    .isFirstParticipation(flat.getTrainingCnt() == 0 && flat.getCompetitionCnt() == 0)
                    .hopePartner(flat.getHopePartner())
                    .additionalComment(flat.getReferContent())
                    .additionalAnswers(Collections.emptyList())
                    .build();
            groupMap.computeIfAbsent(runningGroup, k -> new ArrayList<>()).add(participant);
        }

        List<MatchingWaitingGroup> groups = groupMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> MatchingWaitingGroup.builder()
                        .runningGroup(e.getKey())
                        .totalCount(e.getValue().size())
                        .participants(e.getValue().stream()
                                .sorted(MATCHING_WAITING_PARTICIPANT_ORDER)
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());

        return MatchingWaitingResponse.builder()
                .summary(MatchingWaitingResponse.Summary.builder()
                        .waitingCount(flatList.size())
                        .viCount(viCount)
                        .guideCount(guideCount)
                        .build())
                .groups(groups)
                .build();
    }

    public MatchingCompletedResponse getMatchingCompleted(Long eventId) {
        eventRepository.findById(eventId).orElseThrow(NotExistEventException::new);

        List<MatchingCompletedFlatDto> flatList = matchingRepository.findMatchingCompletedByEventId(eventId);

        // viUserId 기준으로 그룹핑하여 VI별 row 구성
        LinkedHashMap<String, MatchingCompletedRow> rowMap = new LinkedHashMap<>();
        for (MatchingCompletedFlatDto flat : flatList) {
            rowMap.computeIfAbsent(flat.getViUserId(), k -> {
                MatchingUser vi = MatchingUser.builder()
                        .userId(flat.getViUserId())
                        .type(flat.getViType())
                        .name(flat.getViName())
                        .applyRecord(flat.getViApplyRecord())
                        .isAttended(flat.getViIsAttended())
                        .recordDegree(flat.getViRecordDegree())
                        .build();
                return MatchingCompletedRow.builder()
                        .vi(vi)
                        .guides(new ArrayList<>())
                        .build();
            });
            MatchingUser guide = MatchingUser.builder()
                    .userId(flat.getGuideUserId())
                    .type(flat.getGuideType())
                    .name(flat.getGuideName())
                    .applyRecord(flat.getGuideApplyRecord())
                    .isAttended(flat.getGuideIsAttended())
                    .recordDegree(flat.getGuideRecordDegree())
                    .build();
            rowMap.get(flat.getViUserId()).getGuides().add(guide);
        }

        // runningGroup(VI hopeTeam) 기준으로 그룹핑
        Map<String, String> viRunningGroupMap = new LinkedHashMap<>();
        for (MatchingCompletedFlatDto flat : flatList) {
            viRunningGroupMap.putIfAbsent(flat.getViUserId(), flat.getViRunningGroup());
        }

        LinkedHashMap<String, List<MatchingCompletedRow>> groupMap = new LinkedHashMap<>();
        for (Map.Entry<String, MatchingCompletedRow> entry : rowMap.entrySet()) {
            String runningGroup = viRunningGroupMap.getOrDefault(entry.getKey(), "");
            groupMap.computeIfAbsent(runningGroup, k -> new ArrayList<>()).add(entry.getValue());
        }

        List<MatchingCompletedGroup> groups = groupMap.entrySet().stream()
                .map(e -> MatchingCompletedGroup.builder()
                        .runningGroup(e.getKey())
                        .totalCount(e.getValue().size())
                        .rows(e.getValue())
                        .build())
                .collect(Collectors.toList());

        int completedViCount = rowMap.size();
        int matchedGuideCount = flatList.size();

        return MatchingCompletedResponse.builder()
                .summary(MatchingCompletedResponse.Summary.builder()
                        .completedViCount(completedViCount)
                        .matchedGuideCount(matchedGuideCount)
                        .build())
                .groups(groups)
                .build();
    }

    public void autoMatchUsers(Long eventId) {
        int cnt = (int) Math.ceil((double) eventFormRepository.findAllEventIdAndUserType(eventId, UserType.GUIDE).size()
                / eventFormRepository.findAllEventIdAndUserType(eventId, UserType.VI).size());
        List<Form> guideA = eventFormRepository.findAllEventIdAndUserTypeAndHopeTeam(eventId, UserType.GUIDE, "A");
        List<Form> guideB = eventFormRepository.findAllEventIdAndUserTypeAndHopeTeam(eventId, UserType.GUIDE, "B");
        List<Form> guideC = eventFormRepository.findAllEventIdAndUserTypeAndHopeTeam(eventId, UserType.GUIDE, "C");
        List<Form> guideD = eventFormRepository.findAllEventIdAndUserTypeAndHopeTeam(eventId, UserType.GUIDE, "D");
        List<Form> guideE = eventFormRepository.findAllEventIdAndUserTypeAndHopeTeam(eventId, UserType.GUIDE, "E");

        List<Form> viA = eventFormRepository.findAllEventIdAndUserTypeAndHopeTeam(eventId, UserType.VI, "A");
        List<Form> viB = eventFormRepository.findAllEventIdAndUserTypeAndHopeTeam(eventId, UserType.VI, "B");
        List<Form> viC = eventFormRepository.findAllEventIdAndUserTypeAndHopeTeam(eventId, UserType.VI, "C");
        List<Form> viD = eventFormRepository.findAllEventIdAndUserTypeAndHopeTeam(eventId, UserType.VI, "D");
        List<Form> viE = eventFormRepository.findAllEventIdAndUserTypeAndHopeTeam(eventId, UserType.VI, "E");

        for(int i = 0; i<cnt; i++){
            autoMatchBetweenTwoGroup(0,eventId,viA,guideA);

            autoMatchBetweenTwoGroup(
                    autoMatchBetweenTwoGroup(0,eventId,viB,guideB),eventId,viB,guideA);

            autoMatchBetweenTwoGroup(
                    autoMatchBetweenTwoGroup(
                            autoMatchBetweenTwoGroup(0,eventId,viC,guideC),eventId,viC,guideB),eventId,viC,guideA);

            autoMatchBetweenTwoGroup(
                    autoMatchBetweenTwoGroup(
                            autoMatchBetweenTwoGroup(
                                    autoMatchBetweenTwoGroup(0,eventId,viD,guideD),eventId,viD,guideC),eventId,viD,guideB),eventId,viD,guideA);

            autoMatchBetweenTwoGroup(
                    autoMatchBetweenTwoGroup(
                        autoMatchBetweenTwoGroup(
                                autoMatchBetweenTwoGroup(
                                    autoMatchBetweenTwoGroup(0,eventId,viE,guideE),eventId,viE,guideD),eventId,viE,guideC),eventId,viE,guideB),eventId,viE,guideA);

        }
    }

    public int autoMatchBetweenTwoGroup(int startIdx,Long eventId,List<Form> viList,List<Form> guideList){
        for(int i = startIdx ; startIdx<viList.size() ; i++){
            if(guideList.size()==0) {
                return startIdx;
            }
            Form guideForm = guideList.get(0);
            User guide = userRepository.findUserByUserId(guideForm.getUserId()).orElseThrow(NotExistUserException::new);
            User vi = userRepository.findUserByUserId(viList.get(i).getUserId()).orElseThrow(NotExistUserException::new);

            Matching existMatching = matchingRepository.findByEventIdAndGuideId(eventId, guide.getPrivateId());
            if(existMatching==null) {
                matchingRepository.save(
                        Matching.builder()
                                .eventId(eventId)
                                .guideId(guide.getPrivateId())
                                .viId(vi.getPrivateId())
                                .viRecord(viList.get(i).getApplyRecord())
                                .guideRecord(guideForm.getApplyRecord())
                                .build()
                );
                unMatchingRepository.delete(
                        UnMatching.builder()
                                .eventId(eventId)
                                .privateId(guide.getPrivateId())
                                .build()
                );

                Optional<UnMatching> findVi = unMatchingRepository.findByPrivateIdAndEventId(vi.getPrivateId(),eventId);
                if(!findVi.isEmpty()){
                    unMatchingRepository.delete(findVi.get());
                }
            }

            //출석 되어 있으면 파트너 반영.
            updatePartnerOnAttendance(eventId, guide);

            guideList.remove(0);
            startIdx++;
        }
        return startIdx;
    }

    private void updatePartnerOnAttendance(Long eventId, User guide) {
        Attendance attendance = attendanceRepository.findByEventIdAndPrivateId(eventId, guide.getPrivateId());
        if(attendance != null && attendance.isAttend()) {
            partnerService.setAttendGuidePartner(eventId, guide);
        }
        }

    private static int matchingStatusRowUserTypeOrder(MatchingStatusRow row) {
        if (row.getVi() != null) {
            return userTypeOrder(row.getVi().getType());
        }
        return firstGuide(row)
                .map(MatchingStatusUser::getType)
                .map(EventMatchingService::userTypeOrder)
                .orElse(Integer.MAX_VALUE);
    }

    private static String matchingStatusRowName(MatchingStatusRow row) {
        if (row.getVi() != null) {
            return row.getVi().getName();
        }
        return firstGuide(row)
                .map(MatchingStatusUser::getName)
                .orElse(null);
    }

    private static String matchingStatusRowUserId(MatchingStatusRow row) {
        if (row.getVi() != null) {
            return row.getVi().getUserId();
        }
        return firstGuide(row)
                .map(MatchingStatusUser::getUserId)
                .orElse(null);
    }

    private static Optional<MatchingStatusUser> firstGuide(MatchingStatusRow row) {
        if (row.getGuides() == null || row.getGuides().isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(row.getGuides().get(0));
    }

    private static int userTypeOrder(UserType type) {
        if (type == UserType.VI) {
            return 0;
        }
        if (type == UserType.GUIDE) {
            return 1;
        }
        return Integer.MAX_VALUE;
    }

    /*
    public int autoMatchBetweenTwoGroup(List<Form> vi,List<Form> guide){

        return vi.size();
    }
    */


}
