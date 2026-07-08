package com.guide.run.event.service;

import com.guide.run.event.entity.Event;
import com.guide.run.event.entity.EventForm;
import com.guide.run.event.entity.dto.response.get.*;
import com.guide.run.event.entity.repository.EventFormRepository;
import com.guide.run.event.entity.repository.EventRepository;
import com.guide.run.event.entity.type.CityName;
import com.guide.run.event.entity.type.EventFormStatus;
import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.event.entity.type.EventType;
import com.guide.run.global.exception.event.logic.NotValidKindException;
import com.guide.run.global.exception.event.logic.NotValidSortException;
import com.guide.run.partner.entity.matching.Matching;
import com.guide.run.partner.entity.matching.repository.MatchingRepository;
import com.guide.run.user.entity.type.Role;
import com.guide.run.user.entity.type.UserType;
import com.guide.run.user.entity.user.User;
import com.guide.run.user.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import static com.guide.run.event.entity.type.EventRecruitStatus.*;
import static com.guide.run.event.entity.type.EventType.TOTAL;
import static java.time.temporal.ChronoUnit.DAYS;

@Service
@RequiredArgsConstructor
public class EventGetService {
    private static final int UPCOMING_GUEST_LIMIT = 5;
    private static final int UPCOMING_MEMBER_LIMIT = 5;
    private static final ZoneId SERVICE_ZONE = ZoneId.of("Asia/Seoul");

    private final EventRepository eventRepository;
    private final EventFormRepository eventFormRepository;
    private final UserRepository userRepository;
    private final MatchingRepository matchingRepository;


    public MyEventResponse getMyEvent(String sort, int year,String privateId) {
       List<MyEvent> myEvents;
       if(sort.equals("UPCOMING")){
           myEvents = eventRepository.findMyEventByYear(privateId, year, RECRUIT_ALL);
       }else if(sort.equals("END")){
           myEvents = eventRepository.findMyEventByYear(privateId, year, RECRUIT_END);
       }else{
           throw new NotValidSortException();
       }
       return MyEventResponse.builder().items(myEvents).build();
    }

    public long getAllEventListCount(String sort, EventType type, EventRecruitStatus kind, String privateId, CityName cityName) {
        if (sort.equals("UPCOMING")) {
            return eventRepository.countUpcomingEventList(type.equals(TOTAL) ? null : type, kind, cityName);
        } else if (sort.equals("END")) {
            return eventRepository.countPastEventList(type.equals(TOTAL) ? null : type, cityName);
        } else {
            if (type.equals(TOTAL)) {
                if (kind.equals(RECRUIT_ALL)) {
                    if (cityName == null) {
                        return eventFormRepository.countByPrivateId(privateId);
                    }
                    return eventRepository.countByPrivateIdAndCityName(privateId, cityName);
                } else {
                    return eventRepository.getAllMyEventListCount(null, kind, privateId, cityName);
                }
            } else {
                if (kind.equals(RECRUIT_ALL)) {
                    return eventRepository.getAllMyEventListCount(type, null, privateId, cityName);
                } else {
                    return eventRepository.getAllMyEventListCount(type, kind, privateId, cityName);
                }
            }
        }
    }

    public AllEventResponse getAllEventList(int limit,
                                            int start,
                                            int page,
                                            String sort,
                                            EventType type,
                                            EventRecruitStatus kind,
                                            String userId,
                                            CityName cityName) {
        List<AllEvent> allEvents = new ArrayList<>();
        if(sort.equals("UPCOMING")){
            if(kind.equals(RECRUIT_END))
                throw new NotValidKindException();
            if(type.equals(TOTAL)){
                if(kind.equals(RECRUIT_ALL)){
                    allEvents = eventRepository.upcomingGetAllEventList(limit,start,null,RECRUIT_ALL,cityName);
                }
                else{
                    allEvents = eventRepository.upcomingGetAllEventList(limit,start,null,kind,cityName);
                }
            }else{
                if(kind.equals(RECRUIT_ALL)){
                    allEvents = eventRepository.upcomingGetAllEventList(limit,start,type,RECRUIT_ALL,cityName);
                }
                else{
                    allEvents = eventRepository.upcomingGetAllEventList(limit,start,type,kind,cityName);
                }
            }
        }else if(sort.equals("END")){
            if(type.equals(TOTAL)){
                allEvents = eventRepository.pastGetAllEventList(limit,start,null,cityName);
            }else{
                allEvents = eventRepository.pastGetAllEventList(limit,start,type,cityName);
            }
        }else{
            if(type.equals(TOTAL)){
                if(kind.equals(RECRUIT_ALL)){
                    allEvents = eventRepository.getAllMyEventList(limit,start,null,null,userId,cityName);
                }
                else{
                    allEvents = eventRepository.getAllMyEventList(limit,start,null,kind,userId,cityName);
                }
            }else{
                if(kind.equals(RECRUIT_ALL)){
                    allEvents = eventRepository.getAllMyEventList(limit,start,type,null,userId,cityName);
                }
                else{
                    allEvents = eventRepository.getAllMyEventList(limit,start,type,kind,userId,cityName);
                }
            }
        }

        long totalCount = getAllEventListCount(sort, type, kind, userId, cityName);

        return AllEventResponse.builder()
                .items(allEvents)
                .pagination(AllEventResponse.Pagination.of(page, limit, totalCount))
                .build();
    }

    public UpcomingEventResponse getUpcomingEvents(String privateId) {
        User member = findApprovedMember(privateId);
        if (member == null) {
            return getGuestUpcomingEvents();
        }
        return getMemberUpcomingEvents(member);
    }

    private UpcomingEventResponse getGuestUpcomingEvents() {
        List<AllEvent> allEvents = eventRepository.upcomingGetAllEventList(UPCOMING_GUEST_LIMIT, 0, null, RECRUIT_ALL, null);
        if (allEvents.isEmpty()) {
            return UpcomingEventResponse.guest(List.of());
        }

        List<Long> eventIds = allEvents.stream().map(AllEvent::getId).collect(Collectors.toList());
        Map<Long, Event> eventMap = eventRepository.findAllById(eventIds).stream()
                .collect(Collectors.toMap(Event::getId, event -> event));

        List<UpcomingEventResponse.GuestItem> items = allEvents.stream()
                .map(allEvent -> eventMap.get(allEvent.getId()))
                .filter(Objects::nonNull)
                .map(event -> UpcomingEventResponse.GuestItem.builder()
                        .id(event.getId())
                        .name(event.getName())
                        .dDay(getDDay(event.getStartTime().toLocalDate()))
                        .date(event.getStartTime().toLocalDate())
                        .build())
                .collect(Collectors.toList());

        return UpcomingEventResponse.guest(items);
    }

    private UpcomingEventResponse getMemberUpcomingEvents(User member) {
        String privateId = member.getPrivateId();
        Set<Long> appliedEventIds = eventFormRepository.findAllByPrivateId(privateId).stream()
                .filter(this::isActiveApplication)
                .map(EventForm::getEventId)
                .collect(Collectors.toSet());

        Map<Long, Event> eventMap = new LinkedHashMap<>();
        eventRepository.findAllById(appliedEventIds).forEach(event -> eventMap.put(event.getId(), event));
        eventRepository.findAllByOrganizer(privateId).forEach(event -> eventMap.put(event.getId(), event));

        List<UpcomingEventResponse.MemberItem> items = eventMap.values().stream()
                .filter(this::isMemberUpcomingEvent)
                .sorted(Comparator.comparing(Event::getStartTime))
                .limit(UPCOMING_MEMBER_LIMIT)
                .map(event -> {
                    boolean isOrganizer = privateId.equals(event.getOrganizer());
                    List<UpcomingEventResponse.PartnerItem> partners = isOrganizer ? List.of() : findPartners(event, member);
                    return UpcomingEventResponse.MemberItem.builder()
                            .id(event.getId())
                            .name(event.getName())
                            .dDay(getDDay(event.getStartTime().toLocalDate()))
                            .place(event.getPlace())
                            .scheduleText(formatScheduleText(event))
                            .myPartner(partners.isEmpty() ? null : partners)
                            .build();
                })
                .collect(Collectors.toList());

        return UpcomingEventResponse.member(items);
    }

    private User findApprovedMember(String privateId) {
        if (privateId == null) {
            return null;
        }
        return userRepository.findUserByPrivateId(privateId)
                .filter(user -> user.getRole() == Role.ROLE_USER
                        || user.getRole() == Role.ROLE_COACH
                        || user.getRole() == Role.ROLE_ADMIN)
                .orElse(null);
    }

    private boolean isActiveApplication(EventForm eventForm) {
        return eventForm.getStatus() == null || eventForm.getStatus() == EventFormStatus.APPLIED;
    }

    private boolean isMemberUpcomingEvent(Event event) {
        return event != null
                && event.isApprove()
                && event.getStartTime() != null
                && event.getEndTime() != null
                && event.getEndTime().isAfter(EventTemporalStatusResolver.now());
    }

    private int getDDay(LocalDate date) {
        return (int) DAYS.between(LocalDate.now(SERVICE_ZONE), date);
    }

    private List<UpcomingEventResponse.PartnerItem> findPartners(Event event, User member) {
        if (member.getType() == UserType.GUIDE) {
            Matching matching = matchingRepository.findByEventIdAndGuideId(event.getId(), member.getPrivateId());
            if (matching == null || matching.getViId() == null) {
                return List.of();
            }
            return toPartnerItems(List.of(matching.getViId()));
        }
        if (member.getType() == UserType.VI) {
            List<String> guideIds = matchingRepository.findAllByEventIdAndViId(event.getId(), member.getPrivateId()).stream()
                    .map(Matching::getGuideId)
                    .collect(Collectors.toList());
            return toPartnerItems(guideIds);
        }
        return List.of();
    }

    private List<UpcomingEventResponse.PartnerItem> toPartnerItems(List<String> partnerPrivateIds) {
        List<String> ids = partnerPrivateIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        if (ids.isEmpty()) {
            return List.of();
        }

        Map<String, User> userMap = userRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(User::getPrivateId, user -> user, (left, right) -> left, LinkedHashMap::new));

        return ids.stream()
                .map(userMap::get)
                .filter(Objects::nonNull)
                .map(user -> UpcomingEventResponse.PartnerItem.builder()
                        .type(user.getType())
                        .name(user.getName())
                        .build())
                .collect(Collectors.toList());
    }

    private String formatScheduleText(Event event) {
        LocalDateTime startTime = event.getStartTime();
        return String.format(
                "%d년 %d월 %d일 (%s) %s ~ %s",
                startTime.getYear(),
                startTime.getMonthValue(),
                startTime.getDayOfMonth(),
                startTime.getDayOfWeek().getDisplayName(TextStyle.NARROW, Locale.KOREA),
                formatKoreanTime(startTime),
                formatKoreanTime(event.getEndTime())
        );
    }

    private String formatKoreanTime(LocalDateTime time) {
        if (time == null) {
            return "";
        }
        String period = time.getHour() < 12 ? "오전" : "오후";
        int hour = time.getHour() % 12;
        int displayHour = hour == 0 ? 12 : hour;
        if (time.getMinute() == 0) {
            return period + " " + displayHour + "시";
        }
        return period + " " + displayHour + "시 " + time.getMinute() + "분";
    }

    public EventsSummaryGetResponse getEventsSummary(String userId) {
        int year = LocalDate.now().getYear();

        long totalEventCount = eventRepository.countApprovedEventsByYear(year);
        double totalRunningDistanceKm = eventRepository.sumDistanceByYear(year);

        EventsSummaryGetResponse.PublicSummary publicSummary = EventsSummaryGetResponse.PublicSummary.builder()
                .year(year)
                .totalEventCount(totalEventCount)
                .totalRunningDistanceKm(totalRunningDistanceKm)
                .build();

        EventsSummaryGetResponse.MySummary mySummary = null;
        if (userId != null) {
            long totalParticipationCount = eventRepository.countMyParticipation(userId);
            double myTotalDistanceKm = eventRepository.sumMyParticipationDistance(userId);
            mySummary = EventsSummaryGetResponse.MySummary.builder()
                    .totalParticipationCount(totalParticipationCount)
                    .totalRunningDistanceKm(myTotalDistanceKm)
                    .build();
        }

        return EventsSummaryGetResponse.builder()
                .publicSummary(publicSummary)
                .mySummary(mySummary)
                .build();
    }
}
