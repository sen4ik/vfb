package com.sen4ik.vfb.services;

import com.sen4ik.vfb.base.Constants;
import com.sen4ik.vfb.entities.Contact;
import com.sen4ik.vfb.repositories.ContactsRepository;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.twiml.MessagingResponse;
import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

    @Autowired
    private ContactsService contactsService;

    @Autowired
    private ContactsRepository contactsRepository;

    @Value("${my.phone}")
    private String myPhoneNumber;

    @PostConstruct
    private void init(){
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    public void sendSingleMessage(String to, String messageText) {
        log.info("CALLED: sendSingleMessage()");
        log.info("To: " + contactsService.maskPhoneNumber(to));
        log.info("Message Text: " + messageText);

        Message message = Message
                .creator(new PhoneNumber(to),
                        new PhoneNumber(twilioPhoneNumber),
                        messageText)
                .create();
        log.debug(message.getSid());
    }

    public void sendMessageToGroup(List<PhoneNumber> toNumbers, String messageText) {
        log.info("CALLED: sendMessageToGroup()");
        log.info("To: " + contactsService.maskPhoneNumber(toNumbers));
        log.info("Message Text: " + messageText);

        for(PhoneNumber pn : toNumbers){
            Message message = Message
                    .creator(pn,
                            new PhoneNumber(twilioPhoneNumber),
                            messageText)
                    .create();
            log.debug(message.getSid());
        }
    }

    // https://www.twilio.com/docs/sms/twiml
    public ResponseEntity<String> twilioHook(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        log.info("CALLED: twilioHook()");

        // String signatureHeader = httpServletRequest.getHeader("X-Twilio-Signature");
        String body = httpServletRequest.getParameter("Body");
        String from = httpServletRequest.getParameter("From");

        String fromNumberSanitized = contactsService.sanitizePhoneNumber(from);
        String fromNumberMasked = contactsService.maskPhoneNumber(fromNumberSanitized);
        log.info("From: " + fromNumberMasked);
        log.info("Body: " + body);

        if(body.trim().toLowerCase().equals("yes") || body.trim().toLowerCase().equals("yes.")) {
            log.info("Confirming subscription");

            Optional<Contact> contact = contactsRepository.findByPhoneNumber(fromNumberSanitized);
            if (!contact.isPresent()) {
                return returnResponse(Constants.generalMessage);
            } else {
                com.sen4ik.vfb.entities.Contact currentContact = contact.get();
                if(currentContact.getSubscriptionConfirmed() == 0){
                    currentContact.setSubscriptionConfirmed((byte) 1);
                    contactsRepository.save(currentContact);
                    return returnResponse("Thank you for confirming subscription to VerseFromBible.com! You will now receive bible verses daily.");
                }
                else if(currentContact.getSubscriptionConfirmed() == 1){
                    return returnResponse("You have confirmed your VerseFromBible.com subscription already. If you are having issues, contact us at VerseFromBible.com.");
                }
                else{
                    String msg = "Contact " + fromNumberSanitized + " messaged \"" + body + "\" but subscription is not 1 or 0.";
                    log.warn(msg);
                    sendSingleMessage(myPhoneNumber, msg);
                }
            }
        }
        else if(body.trim().toLowerCase().equals("start") || body.trim().toLowerCase().equals("start.")){
            log.info("Start Subscription");
            return returnResponse("Hi. It looks like you are interested in subscribing for daily Bible verses. Please use Sign Up form on www.VerseFromBible.com.");
        }
        else if(Arrays.asList("STOP", "stop", "Stop").contains(body.trim())){
            log.info("Stop/Remove received");

            Optional<Contact> contact = contactsRepository.findByPhoneNumber(fromNumberSanitized);
            if (contact.isPresent()){
                Contact currentContact = contact.get();
                contactsRepository.delete(currentContact);
                log.info("Deleted " + fromNumberMasked);
            }
        }
        else{
            return returnResponse(Constants.generalMessage);
        }

        return returnEmptyResponse();
    }

    private ResponseEntity<String> returnResponse(String messageText){
        com.twilio.twiml.messaging.Message message = new com.twilio.twiml.messaging.Message.Builder(messageText).build();
        // MessagingResponse response = new MessagingResponse.Builder().message(message).message(message2).build();
        MessagingResponse response = new MessagingResponse.Builder().message(message).build();
        String responseXml = response.toXml();
        log.info(responseXml);
        return ResponseEntity.status(HttpStatus.OK).body(responseXml);
    }

    private ResponseEntity<String> returnEmptyResponse(){
        MessagingResponse response = new MessagingResponse.Builder().build();
        String responseXml = response.toXml();
        log.info(responseXml);
        return ResponseEntity.status(HttpStatus.OK).body(responseXml);
    }
}
