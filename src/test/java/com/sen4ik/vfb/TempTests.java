package com.sen4ik.vfb;

import com.jayway.jsonpath.JsonPath;
import com.sen4ik.vfb.repositories.ActionLogRepository;
import com.sen4ik.vfb.services.*;
import com.telerivet.Contact;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public void tt() {
        jobSchedulerService.checkIfVerseForTomorrowExists();
    }

    @Test
    @Disabled
    public void w() throws IOException {
        bibleApiService.getBibleVerse("Genesis",1,1, null);
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
    public void bookAbbr() throws IOException {
        String host = "api.scripture.api.bible";
        String url = "https://" + host + "/v1";
        OkHttpClient client = new OkHttpClient();

        Request booksRequest = new Request.Builder()
                .url(url + "/bibles/f421fe261da7624f-01/books")
                .header("api-key", "API_KEY_GOES_HERE")
                .build();

        Call call = client.newCall(booksRequest);
        Response response = call.execute();
        String booksJson = response.body().string();
        System.out.println(booksJson + "\n\n");

        net.minidev.json.JSONArray abbrObj = JsonPath.parse(booksJson).read("$.data[*].abbreviation");
        System.out.println(abbrObj.toJSONString());
    }

}
