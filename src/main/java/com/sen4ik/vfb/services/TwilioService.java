package com.sen4ik.vfb.services;

import com.google.common.collect.Range;
import com.sen4ik.vfb.constants.Constants;
import com.sen4ik.vfb.entities.Contact;
import com.sen4ik.vfb.repositories.ContactsRepository;
import com.twilio.Twilio;
import com.twilio.base.ResourceSet;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.twiml.MessagingResponse;
import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

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

    @Value("${my.phone}")
    private String myPhoneNumber;

    @Value("${test.env.prefix}")
    private String testEnvPrefix;

    @Autowired
    private ContactsService contactsService;

    @Autowired
    private ContactsRepository contactsRepository;

    @Autowired
    private ActionsLogService actionsLogService;

    @PostConstruct
    private void init(){
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    List<String> stopWords = Arrays.asList("stop", "stop.", "stopall", "stopall.", "cancel", "cancel.", "unsubscribe", "unsubscribe.", "end", "end.", "quit", "quit.");

    public boolean isPhoneNumberValid(String to){
        boolean valid = true;

        try{
            com.twilio.rest.lookups.v1.PhoneNumber phoneNumber = com.twilio.rest.lookups.v1.PhoneNumber.fetcher(
                    new com.twilio.type.PhoneNumber(to)).setCountryCode("US").fetch();

            phoneNumber = com.twilio.rest.lookups.v1.PhoneNumber.fetcher(
                    new com.twilio.type.PhoneNumber(to)).setCountryCode("CA").fetch();
        }
        catch(ApiException a){
            valid = false;
        }

        return valid;
    }

    public void sendSingleMessage(String to, String messageText) throws ApiException {
        log.info("CALLED: sendSingleMessage()");
        log.info("To: " + contactsService.maskPhoneNumber(to));
        log.info("Message Text: " + messageText);

        if(testEnvPrefix != null && !testEnvPrefix.isEmpty()){
            log.info("testEnvPrefix: " + testEnvPrefix);
            messageText = testEnvPrefix + " " + messageText;
        }

        try {
            Message message = Message
                    .creator(new PhoneNumber(to), new PhoneNumber(twilioPhoneNumber), messageText)
                    .create();
            log.debug(message.getSid());

            actionsLogService.messageSent(to, twilioPhoneNumber, messageText, "sid=" + message.getSid());
        }
        catch (ApiException e) {
            /*
            if (e.getCode().equals(21614) || e.getCode().equals(21211)) {
                log.error(to + " is not a valid mobile number!</br>Please provide valid mobile number.");
            }
            else if(e.getCode().equals(21610)){
                log.error("Attempt to send to unsubscribed recipient!");
            }
            */
            log.error("Error on sending SMS: {}", e.getMessage());
            throw e;
        }
    }

    public void sendMessageToGroup(List<PhoneNumber> toNumbers, String messageText) {
        log.info("CALLED: sendMessageToGroup()");
        log.info("To: " + contactsService.maskPhoneNumber(toNumbers));
        log.info("Message Text: " + messageText);

        for(PhoneNumber pn : toNumbers){
            sendSingleMessage(pn.toString(), messageText);
//            Message message = Message
//                    .creator(pn, new PhoneNumber(twilioPhoneNumber), messageText)
//                    .create();
//            log.debug(message.getSid());
//            actionsLogService.messageSent(pn.toString(), twilioPhoneNumber, messageText, "sid=" + message.getSid());
        }
    }

    // https://www.twilio.com/docs/sms/twiml
    public ResponseEntity<String> twilioHook(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        log.info("CALLED: twilioHook()");

        String body = httpServletRequest.getParameter("Body");
        String from = httpServletRequest.getParameter("From");
        String messageSid = httpServletRequest.getParameter("MessageSid");
        String fromNumberSanitized = contactsService.sanitizePhoneNumber(from);
        String fromNumberMasked = contactsService.maskPhoneNumber(fromNumberSanitized);
        String bodyLowerCase = body.trim().toLowerCase();
        log.info("From: " + fromNumberMasked);
        log.info("Body: " + body);

        actionsLogService.log(null, null, body, twilioPhoneNumber, fromNumberSanitized, ActionsLogService.Actions.received.value, "messageSid=" + messageSid);

        if(bodyLowerCase.equals("yes") || bodyLowerCase.equals("yes.")) {
            log.info("Confirming subscription");

            Optional<Contact> contact = contactsRepository.findByPhoneNumber(fromNumberSanitized);
            if (!contact.isPresent()) {
                return returnResponse(fromNumberSanitized, messageSid, Constants.generalMessage);
            } else {
                com.sen4ik.vfb.entities.Contact currentContact = contact.get();
                if(currentContact.getSubscriptionConfirmed() == 0){
                    currentContact.setSubscriptionConfirmed((byte) 1);
                    contactsRepository.save(currentContact);
                    return returnResponse(fromNumberSanitized, messageSid,"Thank you for confirming the subscription to VerseFromBible.com! You will now receive bible verses daily.");
                }
                else if(currentContact.getSubscriptionConfirmed() == 1){
                    return returnResponse(fromNumberSanitized, messageSid,"You have confirmed your VerseFromBible.com subscription already. If you are having issues, contact us at VerseFromBible.com.");
                }
                else{
                    String msg = "Contact " + fromNumberSanitized + " messaged \"" + body + "\" but subscription is not 1 or 0.";
                    log.warn(msg);
                    sendSingleMessage(myPhoneNumber, msg);
                }
            }
        }
        else if(bodyLowerCase.equals("start") || bodyLowerCase.equals("start.")){
            log.info("Start Subscription");
            return returnResponse(fromNumberSanitized, messageSid, "Hi. It looks like you are interested in subscribing for daily Bible verses. Please use Sign Up form on www.VerseFromBible.com.");
        }
        else if(stopWords.contains(bodyLowerCase)){
            log.info("Stop received");

            Optional<Contact> contact = contactsRepository.findByPhoneNumber(fromNumberSanitized);
            if (contact.isPresent()){
                Contact currentContact = contact.get();
                contactsRepository.delete(currentContact);
                log.info("Deleted " + fromNumberMasked);

                actionsLogService.log(currentContact.getId(), null, null, null, null, ActionsLogService.Actions.deleted.value, null);
            }
        }
        else{
            return returnResponse(fromNumberSanitized, messageSid, Constants.generalMessage);
        }

        return returnEmptyResponse(fromNumberSanitized, messageSid);
    }

    private ResponseEntity<String> returnResponse(String to, String messageSid, String messageText){
        com.twilio.twiml.messaging.Message message = new com.twilio.twiml.messaging.Message.Builder(messageText).build();
        // MessagingResponse response = new MessagingResponse.Builder().message(message).message(message2).build();
        MessagingResponse response = new MessagingResponse.Builder().message(message).build();
        String responseXml = response.toXml();
        log.info(responseXml);

        actionsLogService.messageSent(to, twilioPhoneNumber, messageText, "xml=" + responseXml + "; messageSid=" + messageSid);

        return ResponseEntity.status(HttpStatus.OK).body(responseXml);
    }

    private ResponseEntity<String> returnEmptyResponse(String to, String messageSid){
        MessagingResponse response = new MessagingResponse.Builder().build();
        String responseXml = response.toXml();
        log.info(responseXml);

        actionsLogService.messageSent(to, twilioPhoneNumber, null, "xml=" + responseXml + "; messageSid=" + messageSid);

        return ResponseEntity.status(HttpStatus.OK).body(responseXml);
    }

    public void deleteContactsWhoSentStopMessageToTwilioPhoneNumber(){
        // Here is the issue: if someone stopped but later subscribed, we will remove such contact from db, which is not good.
        // How do we properly find out blocked numbers?
        ResourceSet<Message> messages = Message.reader()
                .setDateSent(Range.greaterThan(new DateTime().minusDays(1)))
                .setTo(twilioPhoneNumber)
                .read();

        for(Message record : messages) {
            String body = record.getBody();
            String bodyLowerCase = body.trim().toLowerCase();
            PhoneNumber from = record.getFrom();
            String fromSanitized = contactsService.sanitizePhoneNumber(from.toString());

            if(stopWords.contains(bodyLowerCase)){
                System.out.println("***");
                System.out.println(record.getDateSent());
                System.out.println(fromSanitized);
                System.out.println("body: " + body);
                // System.out.println("getAccountSid: " + record.getAccountSid());
                System.out.println("getSid: " + record.getSid());
                System.out.println("getStatus: " + record.getStatus());
                System.out.println("\n");
            }

        }
    }
}
