package com.sen4ik.vfb;

import com.sen4ik.vfb.entities.ActionLog;
import com.sen4ik.vfb.repositories.ActionLogRepository;
import com.sen4ik.vfb.services.*;
import com.telerivet.Contact;
import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

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

    @Autowired
    private TwilioService twilioService;

    @Autowired
    private ActionLogRepository actionLogRepository;

    @Autowired
    private ActionsLogService actionsLogService;

    @Value("${my.phone}")
    private String myPhoneNumber;

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

    @Test
    @Disabled
    public void ww() {
        List<Contact> allContacts = telerivetService.getAllContacts();
        for(Contact contact : allContacts){
            System.out.println("***");
            System.out.println(contact.getId());
            System.out.println(contact.getPhoneNumber());
            System.out.println(contact.getSendBlocked());
        }
    }

    @Test
    @Disabled
    public void www() {
        twilioService.sendSingleMessage(myPhoneNumber, "This is test");
    }

    @Test
    @Disabled
    public void wwww() {
        ActionLog al = new ActionLog();
        al.setTo("to");
        al.setFrom("from");
        al.setMessageBody("messageBody");
        al.setNotes("notes");
        al.setAction("message sent");
        al.setVerseId(1);
        al.setUserId(1);
        actionLogRepository.save(al);
    }

    @Test
    @Disabled
    public void wwwww() {
        actionsLogService.messageSent("9168683319", "1112223333", "test", "sid=asd");
    }

    @Test
    @Disabled
    public void o() {
        twilioService.deleteContactsWhoSentStopMessageToTwilioPhoneNumber();
    }

}
