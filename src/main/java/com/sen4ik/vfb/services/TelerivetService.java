package com.sen4ik.vfb.services;

import com.telerivet.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service("telerivet")
@Slf4j
public class Telerivet {

    @Value("${telerivet.api-key}")
    private static String API_KEY;  // from https://telerivet.com/api/keys

    @Value("${telerivet.project-id}")
    private static String PROJECT_ID;

    // @Value("${telerivet.phone-id}")
    // private static String PHONE_ID;

    public void sendSingleMessage(String phoneNumber, String message) throws IOException {
        log.info("CALLED: sendMessage()");
        log.info("phoneNumber: " + phoneNumber);
        log.info("message: " + message);

        TelerivetAPI tr = new TelerivetAPI(API_KEY);
        Project project = tr.initProjectById(PROJECT_ID);

        /*
        project.sendMessage(Util.options(
                "to_number", phoneNumber,
                "phone_id", PHONE_ID,
                "content", message
        ));
        */

        Message sent_msg = project.sendMessage(Util.options(
                "content", message,
                "to_number", phoneNumber
        ));
    }


    public void sendMessageToGroup(String message, Object[] toNumbers) throws IOException {
        TelerivetAPI tr = new TelerivetAPI(API_KEY);
        Project project = tr.initProjectById(PROJECT_ID);

        Broadcast broadcast = project.sendBroadcast(Util.options(
                "content", "hello [[contact.name]]!",
                "to_numbers", toNumbers // new Object[] {"+14155550123", "+14255550234", "+16505550345"}
                //"status_url", "https://example.com/sms_status.php",
                //"status_secret", "secret",
                //"is_template", true
        ));
    }

}
