package com.guide.run.user.service;

import com.guide.run.event.entity.repository.EventRepository;
import com.guide.run.event.entity.Event;
import com.guide.run.event.entity.type.EventType;
import com.guide.run.partner.entity.partner.Partner;
import com.guide.run.user.dto.response.MyActivityEventsResponse;
import com.guide.run.user.dto.response.MyActivityPartnersResponse;
import com.guide.run.global.exception.user.resource.NotExistUserException;
import com.guide.run.partner.entity.partner.repository.PartnerRepository;
import com.guide.run.user.dto.GlobalUserInfoDto;
import com.guide.run.user.entity.user.User;
import com.guide.run.user.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MypageService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final PartnerRepository partnerRepository;

    public GlobalUserInfoDto getGlobalUserInfo(String privateId){
        User user = userRepository.findById(privateId).orElseThrow(
                NotExistUserException::new
        );

        return GlobalUserInfoDto.userToInfoDto(user);
    }

    public MyActivityPartnersResponse getActivityPartners(String privateId, String sort, int page) {
        final int SIZE = 5;
        User currentUser = userRepository.findById(privateId).orElseThrow(NotExistUserException::new);

        List<Partner> partners = partnerRepository.findActivityPartners(privateId, currentUser.getType(), sort, page, SIZE);
        long totalCount = partnerRepository.countActivityPartners(privateId, currentUser.getType());
        int totalPages = (int) Math.ceil((double) totalCount / SIZE);

        Set<String> partnerPrivateIds = new HashSet<>();
        Set<Long> allEventIds = new HashSet<>();
        for (Partner p : partners) {
            String partnerId = currentUser.getType().equals(com.guide.run.user.entity.type.UserType.GUIDE)
                    ? p.getViId() : p.getGuideId();
            partnerPrivateIds.add(partnerId);
            allEventIds.addAll(p.getContestIds());
            allEventIds.addAll(p.getTrainingIds());
        }

        Map<String, User> userMap = userRepository.findAllById(partnerPrivateIds).stream()
                .collect(Collectors.toMap(User::getPrivateId, u -> u));
        Map<Long, Event> eventMap = eventRepository.findAllById(allEventIds).stream()
                .collect(Collectors.toMap(Event::getId, e -> e));

        List<MyActivityPartnersResponse.Item> items = partners.stream().map(p -> {
            String partnerPrivateId = currentUser.getType().equals(com.guide.run.user.entity.type.UserType.GUIDE)
                    ? p.getViId() : p.getGuideId();
            User partnerUser = userMap.get(partnerPrivateId);
            if (partnerUser == null) return null;

            Set<Long> eventIds = new HashSet<>();
            eventIds.addAll(p.getContestIds());
            eventIds.addAll(p.getTrainingIds());

            List<MyActivityPartnersResponse.EventItem> events = eventIds.stream()
                    .map(eventMap::get)
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparing(Event::getStartTime).reversed())
                    .map(MyActivityPartnersResponse.EventItem::new)
                    .collect(Collectors.toList());

            return MyActivityPartnersResponse.Item.builder()
                    .partnerId(partnerUser.getUserId())
                    .name(partnerUser.getName())
                    .type(partnerUser.getType())
                    .eventCount(eventIds.size())
                    .events(events)
                    .build();
        }).filter(Objects::nonNull).collect(Collectors.toList());

        return MyActivityPartnersResponse.builder()
                .items(items)
                .pagination(MyActivityPartnersResponse.Pagination.builder()
                        .page(page)
                        .size(SIZE)
                        .totalCount(totalCount)
                        .totalPages(totalPages)
                        .hasNext(page < totalPages - 1)
                        .build())
                .build();
    }

    public MyActivityEventsResponse getActivityEvents(String privateId, EventType type, String relation, int page) {
        final int SIZE = 10;
        List<MyActivityEventsResponse.Item> items = eventRepository.findActivityEvents(privateId, type, relation, page, SIZE);
        long totalCount = eventRepository.countActivityEvents(privateId, type, relation);
        int totalPages = (int) Math.ceil((double) totalCount / SIZE);
        return MyActivityEventsResponse.builder()
                .items(items)
                .pagination(MyActivityEventsResponse.Pagination.builder()
                        .page(page)
                        .size(SIZE)
                        .totalCount(totalCount)
                        .totalPages(totalPages)
                        .hasNext(page < totalPages - 1)
                        .build())
                .build();
    }

}
