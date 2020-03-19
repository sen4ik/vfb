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
import javax.servlet.http.HttpServletResponse;

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
    ResponseEntity<String> telerivetHook(// @RequestParam("secret") String secret,
            HttpServletRequest request, HttpServletResponse response) throws ServletException {

        log.info("CALLED: telerivetHook()");

        JSONObject jsonError = new JSONObject();
        jsonError.put("messages", "Error");

        // PrintWriter out = response.getWriter();

        if (!webHookSecret.equals(request.getParameter("secret")))
        {
            // out.write("Invalid webhook secret");
            log.error("Invalid secret provided!");
            // response.setStatus(403);
            return sendResponse(jsonError, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        else if ("incoming_message".equals(request.getParameter("event")))
        {
            log.info("Incoming message received");

            String content = request.getParameter("content");
            String fromNumber = request.getParameter("from_number");
            String phoneId = request.getParameter("phone_id");
            String fromNumberSanitized = contactsService.sanitizePhoneNumber(fromNumber);
            String fromNumberMasked = fromNumberSanitized.substring(0, fromNumberSanitized.length() - 4) + "****";

            log.info("content: " + content);
            log.info("fromNumber: " + fromNumberMasked);
            log.info("phoneId: " + phoneId);

            // response.setContentType("application/json");

            try
            {
                JSONArray messages = new JSONArray();
                JSONObject reply = new JSONObject();
                reply.put("content", "Thanks for your message!");
                messages.put(reply);

                JSONObject json = new JSONObject();
                json.put("messages", messages);

                // json.write(out);

                return sendResponse(json, HttpStatus.OK);
            }
            catch (JSONException ex){
                throw new ServletException(ex);
            }
        }

//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
//                .body(json.toString());

        return sendResponse(jsonError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<String> sendResponse(JSONObject json, HttpStatus httpStatus){
        return ResponseEntity.status(httpStatus)
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .body(json.toString());
    }

}
