package com.sen4ik.vfb;

import com.sen4ik.vfb.repositories.ActionLogRepository;
import com.sen4ik.vfb.services.*;
import com.telerivet.Contact;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.IOException;
import java.util.List;

@Slf4j
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TempTests {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

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
    public void tt() {
        jobSchedulerService.checkIfVerseForTomorrowExists();
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
    public void wwwww() {
        actionsLogService.messageSent("9168683319", "1112223333", "test", "sid=asd");
    }

    @Test
    @Disabled
    public void o() {
        twilioService.isPhoneNumberValid("+14084574516");
    }

    @Test
    @Disabled
    public void pass(){
        System.out.println(bCryptPasswordEncoder.encode("Sacramento#2021"));
    }

}
