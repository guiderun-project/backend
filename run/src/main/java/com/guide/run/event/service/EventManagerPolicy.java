package com.guide.run.event.service;

import com.guide.run.event.entity.Event;
import com.guide.run.event.entity.repository.EventRepository;
import com.guide.run.global.exception.event.authorize.NotEventOrganizerException;
import com.guide.run.global.exception.event.resource.NotExistEventException;
import com.guide.run.global.exception.user.resource.NotExistUserException;
import com.guide.run.user.entity.type.Role;
import com.guide.run.user.entity.user.User;
import com.guide.run.user.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventManagerPolicy {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    // 이벤트 운영(매칭/출석) 권한: 주최자 본인, ADMIN, COACH만 허용한다.
    public void requireManager(Long eventId, String requesterPrivateId) {
        Event event = eventRepository.findById(eventId).orElseThrow(NotExistEventException::new);
        User requester = userRepository.findUserByPrivateId(requesterPrivateId)
                .orElseThrow(NotExistUserException::new);

        boolean isOrganizer = requesterPrivateId.equals(event.getOrganizer());
        boolean isAdminOrCoach = requester.getRole() == Role.ROLE_ADMIN
                || requester.getRole() == Role.ROLE_COACH;

        if (!isOrganizer && !isAdminOrCoach) {
            throw new NotEventOrganizerException();
        }
    }
}
