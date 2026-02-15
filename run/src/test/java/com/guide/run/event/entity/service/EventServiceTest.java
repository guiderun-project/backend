package com.guide.run.event.entity.service;

// import com.guide.run.event.entity.Event;
// import com.guide.run.event.entity.repository.EventRepository;
// import com.guide.run.event.entity.type.EventRecruitStatus;
// import com.guide.run.event.entity.type.EventType;
// import com.guide.run.event.service.EventService;
// import org.assertj.core.api.Assertions;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.test.web.servlet.MockMvc;
/*

@AutoConfigureMockMvc
@SpringBootTest
class EventServiceTest {
    @MockBean
    EventService eventService;
    @MockBean
    EventRepository eventRepository;
    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    public void init(){
        eventRepository.deleteAll();
    }

    @Test
    @DisplayName("evnet생성 성공")
    void eventCreateSuccess(){
        Event event = Event.builder()
                .id(1L)
                .organizer("org")
                .name("event")
                .isApprove(true)
                .recruitStatus(EventRecruitStatus.RECRUIT_UPCOMING)
                .type(EventType.TRAINING)
                .maxNumG(10)
                .maxNumV(10)
                .place("house")
                .content("content")
                .build();
        Event saved = eventRepository.save(event);
        Event findEvent = eventRepository.findById(1L).orElse(null);
        Assertions.assertThat(saved).usingRecursiveComparison().isEqualTo(findEvent);
    }
}*/