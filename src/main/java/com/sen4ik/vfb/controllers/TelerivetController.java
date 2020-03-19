package com.sen4ik.vfb.controllers;

import com.sen4ik.vfb.entities.TelerivetIncoming;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
            produces = {MediaType.APPLICATION_JSON_VALUE}
        )
//    public @ResponseBody String telerivetHook(@PathVariable("secret") String secret, MultiValueMap paramMap){
    public @ResponseBody String telerivetHook(
            @RequestParam("secret") String secret,
            @RequestParam("incoming_message") String incoming_message,
            @RequestParam("event") String event,
            @RequestParam("content") String content,
            @RequestParam("content") String from_number){

        log.info("CALLED: telerivetHook()");

        String res = incoming_message + " | " + event + " | " + content + " | " + from_number;
        return res;
    }

}
