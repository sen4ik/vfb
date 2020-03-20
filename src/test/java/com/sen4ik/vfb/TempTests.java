package com.sen4ik.vfb;

import com.sen4ik.vfb.services.BibleApiService;
import com.sen4ik.vfb.services.JobSchedulerService;
import com.sen4ik.vfb.services.TelerivetService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@Slf4j
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TempTests {

    @Autowired
    private JobSchedulerService jobSchedulerService;

    @Autowired
    private TelerivetService telerivetService;

    @Autowired
    private BibleApiService bibleApiService;

    @Test
    @Disabled
    public void t() {
        jobSchedulerService.sendVersesForCurrentHour();
    }

    @Test
    @Disabled
    public void tt() {
        jobSchedulerService.checkIfVerseForTomorrowExists();
    }

    @Test
    @Disabled
    public void ttt() {
        String phoneNumber = "+19168683391";
        phoneNumber = phoneNumber.substring(2, phoneNumber.length());
        log.info(phoneNumber);
    }

    @Test
    @Disabled
    public void tttt() {
        telerivetService.getBlockedContacts();
    }

    @Test
    @Disabled
    public void w() throws IOException {
        bibleApiService.getBibleVerse("Genesis",1,1);
    }

}
