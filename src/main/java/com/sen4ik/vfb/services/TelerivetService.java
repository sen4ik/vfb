package com.sen4ik.vfb.services;

import com.sen4ik.vfb.base.Constants;
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

    public String sendSingleMessage(String phoneNumber, String message) throws IOException {
        log.info("CALLED: sendSingleMessage(\'" + contactsService.maskPhoneNumber(phoneNumber) + "\', \'" + message + "\')");
        Message sent_msg = project.sendMessage(Util.options(
                "content", message,
                "to_number", phoneNumber
        ));
        return sent_msg.getId();
    }

    public String sendMessageToGroup(String message, List<String> toNumbers) throws IOException {
        log.info("CALLED: sendMessageToGroup()");
        log.info("message: " + message);
        log.info("toNumbers: " + contactsService.maskPhoneNumber(toNumbers).toString());
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
        List<com.telerivet.Contact> contacts = project.queryContacts().all();
        return contacts;
    }

    public List<Contact> getBlockedContacts(){
        List<com.telerivet.Contact> blockedContacts = project.queryContacts(Util.options(
                "send_blocked", true
        )).all();
        return blockedContacts;
    }

    public void deleteContact(String contactId) throws IOException {
        log.info("CALLED: deleteContact()");
        Contact contact = project.initContactById(contactId);
        contact.delete();
    }

    public void unblockContact(String contactId) throws IOException {
        log.info("CALLED: unblockContacts()");
        Contact contact = project.initContactById(contactId);
        contact.setSendBlocked(false);
        contact.save();
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
            String fromNumberMasked = contactsService.maskPhoneNumber(fromNumberSanitized);

            log.info("content: " + content);
            log.info("fromNumber: " + fromNumberMasked);
            log.debug("phoneId: " + phoneId);

            if(content.trim().toLowerCase().equals("yes") || content.trim().toLowerCase().equals("yes.")){
                log.info("Confirming subscription");

                Optional<com.sen4ik.vfb.entities.Contact> contact = contactsRepository.findByPhoneNumber(fromNumberSanitized);
                if (!contact.isPresent()){
                    return sendMessageInResponse(Constants.generalMessage); // tested
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
            else if(Arrays.asList("STOP", "stop", "Stop").contains(content.trim())){
                // Telerivet automatically blocks phone numbers who sent STOP message.
                // We wont be able to text such contact back, because telerivet blocked it as soon as it received STOP.
                // We will query telerivet contacts and delete the ones that are blocked from our DB.
                log.info("Stop/Remove received.");
                processBlockedTelerivetContacts();
                /*Optional<Contact> contact = contactsRepository.findByPhoneNumber(fromNumberSanitized);
                if (!contact.isPresent()){
                    return sendMessageInResponse(generalMessage);
                }
                else{
                    Contact currentContact = contact.get();
                    contactsRepository.delete(currentContact);
                    return sendMessageInResponse("You have been completely unsubscribed from VerseFromBible.com");
                }*/
            }
            else{
                log.info("Unexpected message content"); // tested
                return sendMessageInResponse(Constants.generalMessage);
            }
        }

        return sendJsonResponse(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public void processBlockedTelerivetContacts() throws IOException {
        log.info("CALLED: processBlockedTelerivetContacts()");
        List<com.telerivet.Contact> blockedContacts = getBlockedContacts();

        if(blockedContacts.size() > 0){
            for(com.telerivet.Contact contact : blockedContacts){
                String phoneNumber = contact.getPhoneNumber();
                String contactId = contact.getId();
                String sanitizedPhoneNumber = contactsService.sanitizePhoneNumber(phoneNumber);
                log.info("Blocked phone number to be removed: " + contactsService.maskPhoneNumber(sanitizedPhoneNumber));

                Optional<com.sen4ik.vfb.entities.Contact> foundContact = contactsRepository.findByPhoneNumber(sanitizedPhoneNumber);
                if(foundContact.isPresent()){
                    contactsRepository.delete(foundContact.get());
                }

                unblockContact(contactId);

                deleteContact(contactId);
            }
        }
    }

    private ResponseEntity<String> sendJsonResponse(JSONObject json, HttpStatus httpStatus){
        String response;
        if(json == null){
            response = "";
        }
        else{
            response = json.toString();
        }
        return ResponseEntity.status(httpStatus)
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .body(response);
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
