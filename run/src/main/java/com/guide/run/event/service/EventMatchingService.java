package com.guide.run.event.service;

import com.guide.run.event.entity.dto.response.match.*;
import com.guide.run.event.entity.repository.EventRepository;
import com.guide.run.global.exception.event.resource.NotExistEventException;
import com.guide.run.global.exception.user.resource.NotExistUserException;
import com.guide.run.partner.entity.matching.Matching;
import com.guide.run.partner.entity.matching.UnMatching;
import com.guide.run.partner.entity.matching.repository.MatchingRepository;
import com.guide.run.partner.entity.matching.repository.UnMatchingRepository;
import com.guide.run.user.entity.type.UserType;
import com.guide.run.user.entity.user.User;
import com.guide.run.user.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventMatchingService {
    private final UnMatchingRepository unMatchingRepository;
    private final MatchingRepository matchingRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    @Transactional
    public void matchUser(Long eventId, String viId, String userId) {
        eventRepository.findById(eventId).orElseThrow(NotExistEventException::new);
        User vi = userRepository.findUserByUserId(viId).orElseThrow(NotExistUserException::new);
        User guide = userRepository.findUserByUserId(userId).orElseThrow(NotExistUserException::new);
        Matching existGuide = matchingRepository.findByEventIdAndGuideId(eventId, userId);
        if(existGuide!=null){
            matchingRepository.delete(existGuide);
            long countMatchedGuideForVi = matchingRepository.countByEventIdAndViId(eventId, existGuide.getViId());
            if(countMatchedGuideForVi==0){
                User unMatchingVi = userRepository.findUserByUserId(existGuide.getViId()).orElseThrow(NotExistUserException::new);
                unMatchingRepository.save(
                        UnMatching.builder()
                                .eventId(eventId)
                                .privateId(unMatchingVi.getPrivateId())
                                .build()
                );
            }
        }
        matchingRepository.save(
                Matching.builder()
                        .eventId(eventId)
                        .guideId(guide.getUserId())
                        .viId(vi.getUserId())
                        .viRecord(vi.getRecordDegree())
                        .guideRecord(guide.getRecordDegree())
                        .build()
        );
        unMatchingRepository.delete(
                UnMatching.builder()
                        .eventId(eventId)
                        .privateId(guide.getPrivateId())
                        .build()
        );
        Optional<UnMatching> findVi = unMatchingRepository.findByPrivateId(vi.getPrivateId());
        if(!findVi.isEmpty()){
            unMatchingRepository.delete(findVi.get());
        }

    }

    @Transactional
    public void deleteMatchUser(Long eventId, String viId, String userId) {
        eventRepository.findById(eventId).orElseThrow(NotExistEventException::new);
        User vi = userRepository.findUserByUserId(viId).orElseThrow(NotExistUserException::new);
        User guide = userRepository.findUserByUserId(userId).orElseThrow(NotExistUserException::new);
        matchingRepository.delete(
                Matching.builder()
                        .eventId(eventId)
                        .guideId(guide.getUserId())
                        .viId(vi.getUserId())
                        .viRecord(vi.getRecordDegree())
                        .guideRecord(guide.getRecordDegree())
                        .build()
        );
        unMatchingRepository.save(
                UnMatching.builder()
                        .eventId(eventId)
                        .privateId(guide.getPrivateId())
                        .build()
        );
        Optional<Matching> findVi = matchingRepository.findByViId(viId);
        if(findVi.isEmpty()){
            unMatchingRepository.save(
                    UnMatching.builder()
                            .eventId(eventId)
                            .privateId(vi.getPrivateId())
                            .build()
            );
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
        return MatchedGuideCount.builder()
                .guide(matchingRepository.findAllByEventIdAndViId(eventId,viId).size()).build();
    }

    public MatchedGuideList getMatchedGuideList(Long eventId, String viId) {
        eventRepository.findById(eventId).orElseThrow(NotExistEventException::new);
        return MatchedGuideList.builder()
                .guide(matchingRepository.findAllMatchedGuideByEventIdAndViId(eventId,viId))
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
}
