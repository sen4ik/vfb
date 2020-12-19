package com.sen4ik.vfb.controllers;

import com.sen4ik.vfb.constants.Views;
import com.sen4ik.vfb.entities.User;
import com.sen4ik.vfb.repositories.UserRepository;
import com.sen4ik.vfb.services.currentUser.CurrentUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@Slf4j
public class ProfileController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    CurrentUserService currentUserService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;/**/

    @GetMapping("/" + Views.profile)
    public String getProfile() {
        return Views.profile;
    }

    @PostMapping("/" + Views.profile)
    public String profile() {
        return Views.profile;
    }

    @PostMapping(value = "/admin/update_profile")
    public RedirectView updateProfile(User user, RedirectAttributes redirectAttributes) {

        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        redirectAttributes.addFlashAttribute("updateProfileSuccessMessage", "All good");
        RedirectView redirect = new RedirectView("/" + Views.profile);
        redirect.setExposeModelAttributes(false);
        return redirect;
    }

    @ModelAttribute("user")
    private User getCurrentUser(){
        return currentUserService.getCurrentUser();
    }

}
