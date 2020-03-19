package com.sen4ik.vfb.controllers;

import com.sen4ik.vfb.entities.Contact;
import com.sen4ik.vfb.repositories.ContactsRepository;
import com.sen4ik.vfb.services.ContactsService;
import com.sen4ik.vfb.services.TelerivetService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Controller
@Slf4j
@RequestMapping(path = "/telerivet")
public class TelerivetController {

    @Value("${telerivet.webhook.secret}")
    private String webHookSecret;

    @Value("${my.phone}")
    private String myPhoneNumber;

    @Value("${vfb.debug}")
    private Boolean debug;

    @Autowired
    ContactsService contactsService;

    @Autowired
    TelerivetService telerivetService;

    @Autowired
    private ContactsRepository contactsRepository;

    String generalMessage = "Hi, I am VerseFromBible.com bot. If you want to subscribe - go to www.VerseFromBible.com. If you want to stop your subscription - reply STOP.";

    /*
    @GetMapping(path="/hook")
    @ResponseBody
    public String getHook() {
        log.info("CALLED: getHook()");
        return "getHook";
    }
    */

    @PostMapping(
            path = "/hook",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
        )
    public @ResponseBody
    ResponseEntity<String> telerivetHook(HttpServletRequest request) throws ServletException, IOException {

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
            String fromNumberMasked = fromNumberSanitized.substring(0, fromNumberSanitized.length() - 4) + "****";

            log.info("content: " + content);
            log.info("fromNumber: " + fromNumberMasked);
            log.info("phoneId: " + phoneId);

            if(content.trim().toLowerCase().equals("yes")){
                log.info("Confirming subscription");

                Optional<Contact> contact = contactsRepository.findByPhoneNumber(fromNumberSanitized);
                if (!contact.isPresent()){
                    return sendMessageInResponse(generalMessage);
                }
                else{
                    Contact currentContact = contact.get();
                    if(currentContact.getSubscriptionConfirmed() == 0){
                        currentContact.setSubscriptionConfirmed((byte) 1);
                        contactsRepository.save(currentContact);
                        return sendMessageInResponse("Thank you for confirming subscription to VerseFromBible.com! You will now receive bible verses daily.");
                    }
                    else if(currentContact.getSubscriptionConfirmed() == 1){
                        return sendMessageInResponse("You have confirmed your VerseFromBible.com subscription already. If you are having issues, contact us at VerseFromBible.com.");
                    }
                    else{
                        String msg = "Contact " + fromNumberSanitized + " messaged \"" + content + "\" but subscription is not 1 or 0.";
                        log.warn(msg);
                        telerivetService.sendSingleMessage(myPhoneNumber, msg);
                    }
                }

            }
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
            else{
                log.info("Unexpected message content");
                return sendMessageInResponse(generalMessage);
            }
        }

        return sendJsonResponse(jsonError, HttpStatus.INTERNAL_SERVER_ERROR);
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
