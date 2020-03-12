package com.sen4ik.vfb;

import com.sen4ik.vfb.services.JobSchedulerService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TempTests {

    @Autowired
    private JobSchedulerService jobSchedulerService;

    @Test
    public void t() throws Exception {
        jobSchedulerService.sendVersesForCurrentHour();
    }

    @Test
    public void tt() {
        jobSchedulerService.checkIfVerseForTomorrowExists();
    }

}