package com.sen4ik.vfb.services;

import com.twilio.Twilio;
import com.twilio.twiml.MessagingResponse;
import com.twilio.twiml.TwiMLException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Service
@Component
@Slf4j
public class TwilioService {

    @Value("${twilio.sid}")
    private String ACCOUNT_SID;

    @Value("${twilio.auth-token}")
    private String AUTH_TOKEN;

    @Value("${twilio.phone-number}")
    private String twilioPhoneNumber;

    @PostConstruct
    private void init(){
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    public void sendSingleMessage(String to, String messageText) {
        log.info("CALLED: sendSingleMessage()");
        Message message = Message
                .creator(new PhoneNumber(to), // to
                        new PhoneNumber(twilioPhoneNumber), // from
                        messageText)
                .create();
        log.debug(message.getSid());
    }

    public void sendMessageToList(List<PhoneNumber> toNumbers, String messageText) {
        log.info("CALLED: sendMessageToList()");

        for(PhoneNumber pn : toNumbers){
            Message message = Message
                    .creator(pn,
                            new PhoneNumber(twilioPhoneNumber),
                            messageText)
                    .create();
            log.debug(message.getSid());
        }
    }

    public ResponseEntity<String> twilioHook(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

        log.info("CALLED: twilioHook()");

        // https://www.twilio.com/docs/sms/twiml

        String signatureHeader = httpServletRequest.getHeader("X-Twilio-Signature");
        log.info("signatureHeader: " + signatureHeader);

        String body = httpServletRequest.getParameter("Body");
        String from = httpServletRequest.getParameter("From");
        log.info(body);
        log.info(from);

        com.twilio.twiml.messaging.Message message = new com.twilio.twiml.messaging.Message.Builder("Hi there 1").build();
        com.twilio.twiml.messaging.Message message2 = new com.twilio.twiml.messaging.Message.Builder("Hi there 2").build();
        MessagingResponse response = new MessagingResponse.Builder()
                .message(message).message(message2).build();

        try {
            System.out.println(response.toXml());
        } catch (TwiMLException e) {
            e.printStackTrace();
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(response.toXml());
    }
}
