package com.sen4ik.vfb.controllers;

import com.sen4ik.vfb.entities.TelerivetIncoming;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
            consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    @ResponseBody
    public String telerivetHook(@RequestBody TelerivetIncoming telerivetIncoming){
        log.info("CALLED: telerivetHook()");

        String receivedSecret = telerivetIncoming.getSecret();

        if (!webHookSecret.equals(receivedSecret)){

        }

        return "postHook" + receivedSecret;
    }

}
