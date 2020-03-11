package com.sen4ik.vfb.controllers;

import com.sen4ik.vfb.entities.VersesEntity;
import com.sen4ik.vfb.repositories.VersesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@Slf4j
public class AdminController {

    @Autowired
    private VersesRepository versesRepository;

    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }

    @InitBinder
    public void initBinder(WebDataBinder binder){
        binder.registerCustomEditor(Date.class,
                new CustomDateEditor(new SimpleDateFormat("MM/dd/yyyy"), true, 10));
    }

    @PostMapping("/add_verse")
    public RedirectView addContact(@Valid VersesEntity verse, BindingResult result, RedirectAttributes redirectAttributes) {

        if(result.hasErrors()) {
            log.error(result.getAllErrors().toString());
            return new RedirectView("error");
        }

        redirectAttributes.addFlashAttribute("sectionId", "add_verse");

        versesRepository.save(verse);

        redirectAttributes.addFlashAttribute("addVerseSuccessMessage", "Verse was added!");

        return new RedirectView("admin");
    }

    @ModelAttribute("verse")
    private VersesEntity getVerse(){
        return new VersesEntity();
    }

}
