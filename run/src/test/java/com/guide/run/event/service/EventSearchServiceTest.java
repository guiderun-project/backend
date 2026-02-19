package com.guide.run.event.service;

import com.guide.run.event.entity.Event;
import com.guide.run.event.entity.dto.response.search.SearchAllEvent;
import com.guide.run.event.entity.repository.EventRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


@SpringBootTest
class EventSearchServiceTest {
    @Autowired
    EventSearchService eventSearchService;

    @Autowired
    EventRepository eventRepository;

    private Long firstEventId;
    private Long secondEventId;
    private Long thirdEventId;

    @BeforeEach
    public void init(){
        eventRepository.deleteAll();
        firstEventId = eventRepository.save(Event.builder()
                .name("테스트")
                .content("중입니다.")
                .build()).getId();
        secondEventId = eventRepository.save(Event.builder()
                .name("토스트")
                .content("아닙니다.")
                .build()).getId();
        thirdEventId = eventRepository.save(Event.builder()
                .name("ㅇㅇ")
                .content("스트라이크")
                .build()).getId();
    }

    @Test
    @DisplayName("전체 이벤트 검색 개수")
    void getSearchAllEventsCount() {
        Assertions.assertThat(eventSearchService.getSearchAllEventsCount("스트").getCount()).isEqualTo(3);
    }

    @Test
    @DisplayName("전체 이벤트 검색은 최신순으로 조회된다")
    void getSearchAllEventsByLatest() {
        List<SearchAllEvent> events = eventSearchService.getSearchAllEvents(0, 10, "스트");

        Assertions.assertThat(events).extracting(SearchAllEvent::getEventId)
                .containsExactly(thirdEventId, secondEventId, firstEventId);
    }
}
