package ru.banking.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;

import java.time.Duration;

@Configuration
public class SchedulingConfig {

    @Value("${period}")
    private Integer period;

    @Value("${delay}")
    private Integer delay;

    @Bean
    public PeriodicTrigger periodicTrigger() {
        PeriodicTrigger periodicTrigger = new PeriodicTrigger(Duration.ofMillis(period));
        periodicTrigger.setInitialDelay(Duration.ofMillis(delay));
        return periodicTrigger;
    }

    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(5);
        threadPoolTaskScheduler.setThreadNamePrefix("ThreadPool");
        return threadPoolTaskScheduler;
    }
}
