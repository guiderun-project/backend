package com.guide.run.event.service;

import com.guide.run.event.entity.Event;
import com.guide.run.event.entity.repository.EventRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EventSearchServiceTest {
    @MockBean
    EventSearchService eventSearchService;

    EventRepository eventRepository;

    @BeforeEach
    public void init(){
        eventRepository.deleteAll();
        eventRepository.save(Event.builder()
                .id(1L)
                .name("테스트")
                .content("중입니다.")
                .build());
        eventRepository.save(Event.builder()
                .id(2L)
                .name("토스트")
                .content("아닙니다.")
                .build());
        eventRepository.save(Event.builder()
                .id(3L)
                .name("ㅇㅇ")
                .content("스트라이크")
                .build());
    }

    @Test
    @DisplayName("전체 이벤트 검색 개수")
    void getSearchAllEventsCount() {
        List<Event> events = eventRepository.findAll();
        eventRepository.save(Event.builder()
                .id(1L)
                .name("테스트")
                .content("중입니다.")
                .build());
        eventRepository.save(Event.builder()
                .id(2L)
                .name("토스트")
                .content("아닙니다.")
                .build());
        eventRepository.save(Event.builder()
                .id(3L)
                .name("ㅇㅇ")
                .content("스트라이크")
                .build());
        int count = 0;
        for (Event e : events)
            count++;
        Assertions.assertThat(3).isEqualTo(count);
       // Assertions.assertThat(3).isEqualTo(eventSearchService.getSearchAllEventsCount("스트").getCount());
    }

    @Test
    @DisplayName("전체 이벤트 검색")
    void getSearchAllEvents() {
    }


}