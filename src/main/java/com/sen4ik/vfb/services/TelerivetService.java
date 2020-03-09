package com.sen4ik.vfb.services;

import com.telerivet.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Component
@Slf4j
public class TelerivetService {

    @Value("${telerivet.api-key}")
    private String API_KEY;

    @Value("${telerivet.project-id}")
    private String PROJECT_ID;

    public TelerivetService(){
    }

    public String sendSingleMessage(String phoneNumber, String message) throws IOException {
        log.info("CALLED: sendSingleMessage(\'" + phoneNumber + "\', \'" + message + "\')");

        TelerivetAPI tr = new TelerivetAPI(API_KEY);
        Project project = tr.initProjectById(PROJECT_ID);

        Message sent_msg = project.sendMessage(Util.options(
                "content", message,
                "to_number", phoneNumber
        ));

        return sent_msg.getId();
    }


    public String sendMessageToGroup(String message, Object[] toNumbers) throws IOException {
        log.info("CALLED: sendMessageToGroup(\'" + message + "\', \'" + toNumbers.toString() + "\')");

        TelerivetAPI tr = new TelerivetAPI(API_KEY);
        Project project = tr.initProjectById(PROJECT_ID);

        Broadcast broadcast = project.sendBroadcast(Util.options(
                "content", message,
                "to_numbers", toNumbers // new Object[] {"+14155550123", "+14255550234", "+16505550345"}
                //"status_url", "https://example.com/sms_status.php",
                //"status_secret", "secret",
                //"is_template", true
        ));

        return broadcast.getId();
    }

}
