package com.guide.run;


import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling // Spring 스케쥴링 기능 사용
public class RunApplication {
	public static void main(String[] args) {
		SpringApplication.run(RunApplication.class, args);
	}

}
