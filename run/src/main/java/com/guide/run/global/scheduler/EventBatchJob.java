package com.guide.run.global.scheduler;

import com.guide.run.event.entity.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile("batch") // 이 클래스를 batch 프로파일에서만 활성화
public class EventBatchJob {

    private final EventRepository eventRepository;

    @Scheduled(cron = "0 0 * * * *") // 매시 0분 0초에 실행
    @Transactional
    public void updateClosedEvents() {
        long updated = eventRepository.updateRecruitEndForClosedEvents();
        if(updated>0)
            log.info("배치 완료 recruitClose && eventEnd -> recruitEnd 로 상태 변경: {} 개 이벤트 업데이트됨", updated);
    }
}