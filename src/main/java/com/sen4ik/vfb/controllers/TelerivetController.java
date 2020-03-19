package com.sen4ik.vfb.controllers;

import com.sen4ik.vfb.services.ContactsService;
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
import java.util.Arrays;

@Controller
@Slf4j
@RequestMapping(path = "/telerivet")
public class TelerivetController {

    @Value("${telerivet.webhook.secret}")
    private String webHookSecret;

    @Autowired
    ContactsService contactsService;

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
    ResponseEntity<String> telerivetHook(HttpServletRequest request) throws ServletException {

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

            Arrays.asList("REMOVE", "remove", "STOP", "stop");

            if(Arrays.asList("YES", "yes").contains(content.trim())){
                log.info("Confirming subscription");



                return sendMessageInResponse("Thank you for confirming subscription to VerseFromBible.com! You will now receive bible verses daily.");
            }
            else if(Arrays.asList("REMOVE", "remove", "STOP", "stop").contains(content.trim())){
                log.info("Unsubscribe");


                return sendMessageInResponse("You have been completely unsubscribed from VerseFromBible.com");
            }
            else{
                log.info("Unexpected message content");
                return sendMessageInResponse("Hi, I am VerseFromBible.com bot. If you want to stop your subscription - reply STOP. If you want to subscribe - go to www.VerseFromBible.com.");
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
