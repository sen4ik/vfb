package com.sen4ik.vfb.base;

import com.sen4ik.vfb.services.JobSchedulerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JobScheduler {

    @Autowired
    private JobSchedulerService jobSchedulerService;

    // https://stackoverflow.com/questions/30887822/spring-cron-vs-normal-cron

    @Scheduled(cron = "0 0 * * * *") // every hour
    // @Scheduled(cron = "0 */1 * * * *")
    public void sendOutVerses() throws Exception {
        jobSchedulerService.sendVersesForCurrentHour();
    }

    @Scheduled(cron = "0 0 8 * * *") // every day at 8 AM
    // @Scheduled(cron = "0 */1 * * * *") // every minutes
    public void checkVersesForTomorrowExists() {
        jobSchedulerService.checkIfVerseForTomorrowExists();
    }

}
