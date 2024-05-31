package com.guide.run.event.service;

import com.guide.run.event.entity.dto.response.calender.*;
import com.guide.run.event.entity.repository.EventRepository;
import com.guide.run.event.entity.type.EventType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventCalenderService {
    private final EventRepository eventRepository;
    public MyEventsOfMonthOfCalender getMyEventsOfMonth(int year, int month, String privateId){
        YearMonth yearMonth = YearMonth.of(year, month);
        List<MyEventOfMonthOfCalendar> result = new ArrayList<>();
        for(int i =1; i<= yearMonth.lengthOfMonth();i++){
            result.add(
                    MyEventOfMonthOfCalendar.builder().
                            day(i)
                            .competition(false)
                            .training(false)
                            .build()
            );
        }
        List<MyEventOfMonth> myEventsOfMonth = eventRepository.findMyEventsOfMonth(yearMonth.atDay(1).atStartOfDay(),
                yearMonth.atEndOfMonth().atTime(LocalTime.MAX),
                privateId);

        for(MyEventOfMonth myEventOfMonth : myEventsOfMonth){
            if(myEventOfMonth.getEventType().equals(EventType.COMPETITION)){
                result.get(myEventOfMonth.getStartTime().getDayOfMonth()-1).setCompetition(true);
            }else {
                result.get(myEventOfMonth.getStartTime().getDayOfMonth()-1).setTraining(true);
            }
        }
        return MyEventsOfMonthOfCalender.builder().
            result(result).build();
    }
    public MyEventsOfDayOfCalendar getMyEventsOfDay(LocalDate localDate, String privateId){
        List<MyEventOfDayOfCalendar> myEventsOfDay = eventRepository.findMyEventsOfDay(localDate.atStartOfDay(),
                localDate.atTime(LocalTime.MAX), privateId);
        return MyEventsOfDayOfCalendar.builder().
                items(myEventsOfDay).build();
    }
}
