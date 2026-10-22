package com.guide.run.event.service;

import com.guide.run.attendance.repository.AttendanceRepository;
import com.guide.run.attendance.service.AttendService;
import com.guide.run.global.exception.event.authorize.NotEventOrganizerException;
import com.guide.run.partner.service.PartnerService;
import com.guide.run.user.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class EventAttendServiceTest {

    private static final String MALLORY = "mallory";

    @Mock private AttendanceRepository attendanceRepository;
    @Mock private UserRepository userRepository;
    @Mock private AttendService attendService;
    @Mock private PartnerService partnerService;
    @Mock private EventManagerPolicy eventManagerPolicy;

    @InjectMocks private EventAttendService eventAttendService;

    @Test
    @DisplayName("출석 처리: 주최자/ADMIN/COACH가 아니면 거부한다")
    void confirmAttendRequiresManager() {
        lenient().doThrow(new NotEventOrganizerException())
                .when(eventManagerPolicy).requireManager(1L, MALLORY);

        assertThatThrownBy(() -> eventAttendService.confirmAttend(1L, "target", MALLORY))
                .isInstanceOf(NotEventOrganizerException.class);
    }

    @Test
    @DisplayName("출석 취소: 주최자/ADMIN/COACH가 아니면 거부한다")
    void cancelAttendRequiresManager() {
        lenient().doThrow(new NotEventOrganizerException())
                .when(eventManagerPolicy).requireManager(1L, MALLORY);

        assertThatThrownBy(() -> eventAttendService.cancelAttend(1L, "target", MALLORY))
                .isInstanceOf(NotEventOrganizerException.class);
    }

    @Test
    @DisplayName("출석 현황 조회: 주최자/ADMIN/COACH가 아니면 거부한다")
    void getEventAttendanceRequiresManager() {
        lenient().doThrow(new NotEventOrganizerException())
                .when(eventManagerPolicy).requireManager(1L, MALLORY);

        assertThatThrownBy(() -> eventAttendService.getEventAttendance(1L, MALLORY))
                .isInstanceOf(NotEventOrganizerException.class);
    }
}
