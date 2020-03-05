package com.sen4ik.vfb.controllers;

import com.sen4ik.vfb.entities.ContactsEntity;
import com.sen4ik.vfb.entities.VersesEntity;
import com.sen4ik.vfb.repositories.ContactsRepository;
import com.sen4ik.vfb.repositories.VersesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.Date;
import java.util.Optional;

@Controller
public class HomeController {

    // https://www.baeldung.com/spring-boot-crud-thymeleaf

    @Autowired
    private VersesRepository versesRepository;

    @Autowired
    private ContactsRepository contactsRepository;

    @GetMapping("/")
    public String main(ContactsEntity contact, Model model) {

        Optional<VersesEntity> verse = versesRepository.findByDate(new Date());
        if (!verse.isPresent()){
            // return "error";
            model.addAttribute("verse_body", "For God so loved the world, that he gave his only Son, that whoever believes in him should not perish but have eternal life.");
            model.addAttribute("verse_location", "John 3:16");
        }
        else{
            model.addAttribute("verse_body", verse.get().getEnEsv());
            model.addAttribute("verse_location", verse.get().getEnVerseLocation());
        }

        model.addAttribute("contact", contact);

        return "index"; // view
    }

    @PostMapping("/signup")
    public String addUser(@Valid ContactsEntity contact, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "error";
        }

        contactsRepository.save(contact);

        model.addAttribute("contact", contact);

        // model.addAttribute("contacts", contactsRepository.findAll());

        return "index";
    }

    /*
    // hello?name=kotlin
    @GetMapping("/hello")
    public String mainWithParam(
        @RequestParam(name = "name", required = false, defaultValue = "") String name,
        Model model) {

        model.addAttribute("message", name);

        return "index"; // view
    }
    */

}
