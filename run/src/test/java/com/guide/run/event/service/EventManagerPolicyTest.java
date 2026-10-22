package com.guide.run.event.service;

import com.guide.run.event.entity.Event;
import com.guide.run.event.entity.repository.EventRepository;
import com.guide.run.global.exception.event.authorize.NotEventOrganizerException;
import com.guide.run.global.exception.event.resource.NotExistEventException;
import com.guide.run.user.entity.type.Role;
import com.guide.run.user.entity.user.User;
import com.guide.run.user.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventManagerPolicyTest {

    private static final Long EVENT_ID = 1L;
    private static final String ORGANIZER = "organizer-private";

    @Mock private EventRepository eventRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private EventManagerPolicy policy;

    private void givenRequester(String requesterPrivateId, Role role) {
        Event event = Event.builder().organizer(ORGANIZER).build();
        User requester = User.builder().privateId(requesterPrivateId).role(role).build();
        lenient().when(eventRepository.findById(EVENT_ID)).thenReturn(Optional.of(event));
        lenient().when(userRepository.findUserByPrivateId(requesterPrivateId)).thenReturn(Optional.of(requester));
    }

    @Test
    @DisplayName("주최자 본인은 운영 권한이 있다")
    void organizerIsAllowed() {
        givenRequester(ORGANIZER, Role.ROLE_USER);

        assertThatCode(() -> policy.requireManager(EVENT_ID, ORGANIZER)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("ADMIN은 주최자가 아니어도 운영 권한이 있다")
    void adminIsAllowed() {
        givenRequester("admin-private", Role.ROLE_ADMIN);

        assertThatCode(() -> policy.requireManager(EVENT_ID, "admin-private")).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("COACH는 주최자가 아니어도 운영 권한이 있다")
    void coachIsAllowed() {
        givenRequester("coach-private", Role.ROLE_COACH);

        assertThatCode(() -> policy.requireManager(EVENT_ID, "coach-private")).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("주최자도 ADMIN도 COACH도 아닌 일반 회원은 거부된다")
    void plainMemberIsRejected() {
        givenRequester("member-private", Role.ROLE_USER);

        assertThatThrownBy(() -> policy.requireManager(EVENT_ID, "member-private"))
                .isInstanceOf(NotEventOrganizerException.class);
    }

    @Test
    @DisplayName("존재하지 않는 이벤트는 NotExistEventException")
    void missingEventThrows() {
        when(eventRepository.findById(EVENT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> policy.requireManager(EVENT_ID, "anyone"))
                .isInstanceOf(NotExistEventException.class);
    }
}
