package com.sen4ik.vfb.controllers;

import com.sen4ik.vfb.entities.VersesEntity;
import com.sen4ik.vfb.repositories.VersesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.Optional;

@Controller
public class HomeController {

    @Autowired
    private VersesRepository versesRepository;

    @GetMapping("/")
    public String main(Model model) {

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

        return "index"; // view
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
