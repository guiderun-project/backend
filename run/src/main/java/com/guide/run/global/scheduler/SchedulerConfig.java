package com.guide.run.global.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
@Configuration
@Slf4j
public class SchedulerConfig {
    private static final int POOL_SIZE = 4;
    @Bean
    public ThreadPoolTaskScheduler taskScheduler(){
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(POOL_SIZE);
        scheduler.setErrorHandler(t -> log.error("Error in scheduled task", t));
        scheduler.setThreadNamePrefix("My Scheduler - ");
        scheduler.initialize();
        return scheduler;
    }
}
