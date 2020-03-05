package com.sen4ik.vfb.controllers;

import com.sen4ik.vfb.entities.ContactsEntity;
import com.sen4ik.vfb.repositories.ContactsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class SignUpController {

    @Autowired
    private ContactsRepository contactsRepository;

    @PostMapping("/signup")
    public String addUser(@Valid ContactsEntity contact, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "error";
        }

        contactsRepository.save(contact);

        // model.addAttribute("contacts", contactsRepository.findAll());

        return "index?status=confirmation_needed";
    }

}
