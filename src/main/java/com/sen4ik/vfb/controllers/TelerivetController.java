package com.sen4ik.vfb.controllers;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Controller
@Slf4j
@RequestMapping(path = "/telerivet")
public class TelerivetController {

    @Value("${telerivet.webhook.secret}")
    private String webHookSecret;

    /*
    @GetMapping(path="/hook")
    @ResponseBody
    public String getHook() {
        log.info("CALLED: getHook()");
        return "getHook";
    }
    */

    // @RequestMapping(value = "/hook", method = RequestMethod.POST)
    @PostMapping(
            path = "/hook",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
        )
//    public @ResponseBody String telerivetHook(@PathVariable("secret") String secret, MultiValueMap paramMap){
    public @ResponseBody
    ResponseEntity<JSONObject> telerivetHook(
            @RequestParam("secret") String secret, HttpServletRequest request, HttpServletResponse response
//            @RequestParam("incoming_message") String incoming_message,
//            @RequestParam("event") String event,
//            @RequestParam("content") String content,
//            @RequestParam("from_number") String from_number
    ) throws IOException, ServletException {
        log.info("CALLED: telerivetHook()");

        log.info("secret: " + secret);
//        log.info("event: " + event);
//        log.info("content: " + content);
//        log.info("from_number: " + from_number);

//        String res = incoming_message + " | " + event + " | " + content + " | " + from_number;
        PrintWriter out = response.getWriter();

        if (!webHookSecret.equals(request.getParameter("secret")))
        {
            response.setStatus(403);
            out.write("Invalid webhook secret");
            log.info("Invalid webhook secret");
        }
        else if ("incoming_message".equals(request.getParameter("event")))
        {
            log.info("incoming_message");
            String content = request.getParameter("content");
            String fromNumber = request.getParameter("from_number");
            String phoneId = request.getParameter("phone_id");

            log.info(content + " | " + fromNumber + " | " + phoneId);
            // do something with the message, e.g. send an autoreply

            response.setContentType("application/json");

            try
            {
                JSONArray messages = new JSONArray();
                JSONObject reply = new JSONObject();
                reply.put("content", "Thanks for your message!");
                messages.put(reply);

                JSONObject json = new JSONObject();
                json.put("messages", messages);

                json.write(out);

                return ResponseEntity.status(HttpStatus.OK)
                        .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .body(json);
            }
            catch (JSONException ex){
                throw new ServletException(ex);
            }
        }

        JSONObject json = new JSONObject();
        json.put("messages", "Error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .body(json);
    }

}
