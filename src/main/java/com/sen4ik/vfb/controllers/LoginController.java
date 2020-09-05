package com.sen4ik.vfb.controllers;

import com.sen4ik.vfb.constants.Views;
import com.sen4ik.vfb.entities.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;

@Controller
@Slf4j
public class LoginController {

    @GetMapping("/" + Views.login)
    public ModelAndView login(Principal principal) {
        if (principal != null) {
            return new ModelAndView("redirect:" + Views.admin);
        }
        return new ModelAndView(Views.login);
    }

    // BUG: access_denied page is inaccessible
    @GetMapping("/" + Views.accessDenied)
    public String accessDenied(){
        return "/" + Views.accessDenied;
    }

    @ModelAttribute(value = "user")
    public User user(){
        return new User();
    }
}
