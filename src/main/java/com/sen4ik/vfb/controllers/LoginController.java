package com.sen4ik.vfb.controllers;

import com.sen4ik.vfb.entities.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;

@Controller
public class LoginController {

    @GetMapping("/login")
    public ModelAndView login(Principal principal) {
        if (principal != null) {
            return new ModelAndView("redirect:admin/index");
        }
        return new ModelAndView("login");
    }

    @GetMapping("/access_denied")
    public String accessDenied() {
        return "/access_denied";
    }

    @ModelAttribute(value = "user")
    public User user(){
        return new User();
    }
}
