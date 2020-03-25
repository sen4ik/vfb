package com.sen4ik.vfb.controllers;

import com.sen4ik.vfb.services.TwilioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@Slf4j
@RequestMapping(path = "/twilio")
public class TwilioController {

    @Autowired
    TwilioService twilioService;

    @PostMapping(
            path = "/hook",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_XML_VALUE
        )
    @Async
    public @ResponseBody
    ResponseEntity<String> twilioHook(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return twilioService.twilioHook(request, response);
    }

}
