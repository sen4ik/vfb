package com.sen4ik.vfb.scheduler;

import com.sen4ik.vfb.services.JobSchedulerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Component
@Slf4j
public class JobScheduler {

    @Value("${scheduler.enabled}")
    private Boolean schedulerEnabled;

    @Autowired
    private JobSchedulerService jobSchedulerService;

    @PostConstruct
    private void init(){
        if(!schedulerEnabled){
            log.warn("Scheduler is disabled!");
        }
    }

    // https://stackoverflow.com/questions/30887822/spring-cron-vs-normal-cron
    @Scheduled(cron = "0 0 * * * *") // every hour
    // @Scheduled(cron = "0 */1 * * * *")
    public void sendOutVerses() {
        if(schedulerEnabled){
            jobSchedulerService.sendVersesForCurrentHour();
        }
    }

    @Scheduled(cron = "0 0 8 * * *") // every day at 8 AM
    // @Scheduled(cron = "0 */1 * * * *") // every minutes
    public void checkVersesForTomorrowExists() {
        if(schedulerEnabled) {
            jobSchedulerService.checkIfVerseForTomorrowExists();
        }
    }

    @Scheduled(cron = "0 30 * * * *") // every half an hour
    public void checkBlockedNumbers() throws IOException {
        if(schedulerEnabled) {
            jobSchedulerService.processBlockedContacts();
        }
    }

}
