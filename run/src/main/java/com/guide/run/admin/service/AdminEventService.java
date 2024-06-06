package com.guide.run.admin.service;

import com.guide.run.admin.dto.EventDto;
import com.guide.run.admin.dto.EventHistoryDto;
import com.guide.run.admin.dto.EventTypeCountDto;
import com.guide.run.admin.dto.condition.EventSortCond;
import com.guide.run.admin.dto.request.ApprovalEvent;
import com.guide.run.admin.dto.response.event.CurrentEventResponse;
import com.guide.run.admin.dto.response.event.AdminEventResult;
import com.guide.run.event.entity.Event;
import com.guide.run.event.entity.dto.response.get.Count;
import com.guide.run.event.entity.repository.EventRepository;
import com.guide.run.global.converter.TimeFormatter;
import com.guide.run.global.exception.event.resource.NotExistEventException;
import com.guide.run.global.exception.user.resource.NotExistUserException;
import com.guide.run.user.entity.user.User;
import com.guide.run.user.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminEventService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final TimeFormatter timeFormatter;

    public List<EventHistoryDto> getEventHistory(String userId, int start, int limit, String sort){
        User user = userRepository.findUserByUserId(userId).orElseThrow(NotExistUserException::new);
        return eventRepository.getUserEventHistory(user.getPrivateId(), start, limit, sort);
    }

    public Count getEventHistoryCount(String userId, String sort){
        User user = userRepository.findUserByUserId(userId).orElseThrow(NotExistUserException::new);

        return Count.builder()
                .count(eventRepository.getUserEventHistoryCount(user.getPrivateId(), sort))
                .build();
    }

    public EventTypeCountDto getEventTypeCount(String userId) {
        User user = userRepository.findUserByUserId(userId).orElseThrow(NotExistUserException::new);
        return EventTypeCountDto.builder()
                .totalCnt(user.getCompetitionCnt()+user.getTrainingCnt())
                .competitionCnt(user.getCompetitionCnt())
                .trainingCnt(user.getTrainingCnt())
                .build();
    }


    public Count getAllEventCount() {
        return Count.builder()
                .count(eventRepository.getAdminEventCount())
                .build();
    }

    public List<EventDto> getAllEventList(int start, int limit, EventSortCond cond){
        return eventRepository.getAdminEventList(start, limit, cond);
    }

    public String calculatePM(Event e){
        if(e.getEndTime().getHour()>=12){
            return " PM "+(e.getEndTime().getHour()-12);
        }else{
            return " AM "+e.getEndTime().getHour();
        }
    }


    public List<CurrentEventResponse> getCurrentEvents(int start, int limit){
        return eventRepository.findCurrentEvent(start, limit);
    }

    public List<EventDto> searchAllEvent(String text, int start, int limit, EventSortCond cond){
        return eventRepository.searchAdminEvent(text, start, limit, cond);
    }

    public Count searchAllEventCount(String text){

        return Count.builder()
                .count(eventRepository.searchAdminEventCount(text))
                .build();
    }

    public List<EventHistoryDto> searchEventHistory(String userId, String text, int start, int limit){
        User user = userRepository.findUserByUserId(userId).orElseThrow(NotExistUserException::new);
        return eventRepository.searchUserEventHistory(user.getPrivateId(), text, start, limit);
    }

    public Count searchEventHistoryCount(String userId, String text){
        User user = userRepository.findUserByUserId(userId).orElseThrow(NotExistUserException::new);
        return Count.builder()
                .count(eventRepository.searchUserEventHistoryCount(user.getPrivateId(), text))
                .build();
    }

    public void approvalEvent(long eventId, ApprovalEvent approvalEvent){
        Event event = eventRepository.findById(eventId).orElseThrow(NotExistEventException::new);
        event.approvalEvent(approvalEvent.isApproval());
    }

    public AdminEventResult getEventResult(long eventId){
        Event event = eventRepository.findById(eventId).orElseThrow(NotExistEventException::new);
        User user = userRepository.findUserByPrivateId(event.getOrganizer()).orElseThrow(NotExistUserException::new);
        return AdminEventResult.builder()
                .name(event.getName())
                .date(event.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-mm-dd")))
                .type(event.getType())
                .organizer(user.getName())
                .pace(user.getRecordDegree())
                .status(event.getStatus())
                .recruitStatus(event.getRecruitStatus())
                .total(event.getGuideCnt()+event.getViCnt())
                .viCnt(event.getViCnt())
                .guideCnt(event.getGuideCnt())
                //todo : 출석 반영해서 안 온 사람 체크해야 함.
                .build();

    }


}
