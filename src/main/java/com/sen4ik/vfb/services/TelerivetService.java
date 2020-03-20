package com.sen4ik.vfb.services;

import com.sen4ik.vfb.repositories.ContactsRepository;
import com.telerivet.*;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Component
@Slf4j
public class TelerivetService {

    @Value("${telerivet.api-key}")
    private String API_KEY;

    @Value("${telerivet.project-id}")
    private String PROJECT_ID;

    @Value("${telerivet.webhook.secret}")
    private String webHookSecret;

    @Value("${my.phone}")
    private String myPhoneNumber;

    @Autowired
    private ContactsService contactsService;

    @Autowired
    private ContactsRepository contactsRepository;

    TelerivetAPI tr;
    Project project;

    @PostConstruct
    private void init(){
        tr = new TelerivetAPI(API_KEY);
        project = tr.initProjectById(PROJECT_ID);
    }

    String generalMessage = "Hi, I am VerseFromBible.com bot. If you want to subscribe - go to www.VerseFromBible.com. If you want to stop your subscription - reply STOP.";

    public String sendSingleMessage(String phoneNumber, String message) throws IOException {
        log.info("CALLED: sendSingleMessage(\'" + phoneNumber + "\', \'" + message + "\')");

//        TelerivetAPI tr = new TelerivetAPI(API_KEY);
//        Project project = tr.initProjectById(PROJECT_ID);

        Message sent_msg = project.sendMessage(Util.options(
                "content", message,
                "to_number", phoneNumber
        ));

        return sent_msg.getId();
    }

    public String sendMessageToGroup(String message, List<String> toNumbers) throws IOException {
        log.info("CALLED: sendMessageToGroup()");
        log.info("message: " + message);
        log.info("toNumbers: " + toNumbers.toString());

//        TelerivetAPI tr = new TelerivetAPI(API_KEY);
//        Project project = tr.initProjectById(PROJECT_ID);

        Broadcast broadcast = project.sendBroadcast(Util.options(
                "content", message,
                "to_numbers", toNumbers // new Object[] {"+14155550123", "+14255550234", "+16505550345"}
                //"status_url", "https://example.com/sms_status.php",
                //"status_secret", "secret",
                //"is_template", true
        ));

        return broadcast.getId();
    }

    public List<Contact> getAllContacts(){
//        TelerivetAPI tr = new TelerivetAPI(API_KEY);
//        Project project = tr.initProjectById(PROJECT_ID);

        List<com.telerivet.Contact> contacts = project.queryContacts().all();
        return contacts;
    }

    public List<Contact> getBlockedContacts(){

        TelerivetAPI tr = new TelerivetAPI(API_KEY);
        Project project = tr.initProjectById(PROJECT_ID);

        List<com.telerivet.Contact> blockedContacts = project.queryContacts(Util.options(
                "send_blocked", true
        )).all();

        return blockedContacts;
    }

    public ResponseEntity<String> telerivetHook(HttpServletRequest request) throws ServletException, IOException {
        log.info("CALLED: telerivetHook()");

        JSONObject jsonError = new JSONObject();
        jsonError.put("messages", "Error");

        if (!webHookSecret.equals(request.getParameter("secret"))){
            log.error("Invalid secret provided!");
            return sendJsonResponse(jsonError, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        else if ("incoming_message".equals(request.getParameter("event"))){
            log.info("Incoming message received");

            String content = request.getParameter("content");
            String fromNumber = request.getParameter("from_number");
            String phoneId = request.getParameter("phone_id");
            String fromNumberSanitized = contactsService.sanitizePhoneNumber(fromNumber);
            String fromNumberMasked = maskPhoneNumber(fromNumberSanitized);

            log.info("content: " + content);
            log.info("fromNumber: " + fromNumberMasked);
            log.info("phoneId: " + phoneId);

            if(content.trim().toLowerCase().equals("yes")){
                log.info("Confirming subscription");

                Optional<com.sen4ik.vfb.entities.Contact> contact = contactsRepository.findByPhoneNumber(fromNumberSanitized);
                if (!contact.isPresent()){
                    return sendMessageInResponse(generalMessage); // tested
                }
                else{
                    com.sen4ik.vfb.entities.Contact currentContact = contact.get();
                    if(currentContact.getSubscriptionConfirmed() == 0){ // tested
                        currentContact.setSubscriptionConfirmed((byte) 1);
                        contactsRepository.save(currentContact);
                        return sendMessageInResponse("Thank you for confirming subscription to VerseFromBible.com! You will now receive bible verses daily.");
                    }
                    else if(currentContact.getSubscriptionConfirmed() == 1){ // tested: works
                        return sendMessageInResponse("You have confirmed your VerseFromBible.com subscription already. If you are having issues, contact us at VerseFromBible.com.");
                    }
                    else{ // tested
                        String msg = "Contact " + fromNumberSanitized + " messaged \"" + content + "\" but subscription is not 1 or 0.";
                        log.warn(msg);
                        sendSingleMessage(myPhoneNumber, msg);
                    }
                }

            }
            else if(Arrays.asList("Start", "start", "START").contains(content.trim())){
                log.info("Start");
                return sendMessageInResponse("Hi. It looks like you are interested in subscribing for daily Bible verses. Please use Sign Up form on www.VerseFromBible.com.");
            }
            /*
            // Telerivet automatically blocks phone numbers who sent STOP message.
            // We do query for blocked phone numbers every half an hour. Check
            else if(Arrays.asList("REMOVE", "remove", "STOP", "stop", "Remove", "Stop").contains(content.trim())){
                log.info("Unsubscribe");

                Optional<Contact> contact = contactsRepository.findByPhoneNumber(fromNumberSanitized);
                if (!contact.isPresent()){
                    return sendMessageInResponse(generalMessage);
                }
                else{
                    Contact currentContact = contact.get();
                    contactsRepository.delete(currentContact);
                    return sendMessageInResponse("You have been completely unsubscribed from VerseFromBible.com");
                }
            }
            */
            else{
                log.info("Unexpected message content"); // tested
                return sendMessageInResponse(generalMessage);
            }
        }

        return sendJsonResponse(jsonError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public String maskPhoneNumber(String phoneNumber){
        phoneNumber = phoneNumber.substring(0, phoneNumber.length() - 4) + "****";
        return phoneNumber;
    }

    private ResponseEntity<String> sendJsonResponse(JSONObject json, HttpStatus httpStatus){
        return ResponseEntity.status(httpStatus)
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .body(json.toString());
    }

    private ResponseEntity<String> sendMessageInResponse(String message) throws ServletException {
        try
        {
            JSONArray messages = new JSONArray();
            JSONObject reply = new JSONObject();
            reply.put("content", message);
            messages.put(reply);

            JSONObject json = new JSONObject();
            json.put("messages", messages);

            return sendJsonResponse(json, HttpStatus.OK);
        }
        catch (JSONException ex){
            throw new ServletException(ex);
        }
    }

}
