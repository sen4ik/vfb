package com.sen4ik.vfb.controllers;

import com.sen4ik.vfb.constants.Views;
import com.sen4ik.vfb.entities.ActionLog;
import com.sen4ik.vfb.entities.Contact;
import com.sen4ik.vfb.entities.Verse;
import com.sen4ik.vfb.repositories.ActionLogRepository;
import com.sen4ik.vfb.repositories.ContactsRepository;
import com.sen4ik.vfb.repositories.VersesRepository;
import com.sen4ik.vfb.services.BibleApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@Slf4j
public class AdminController {

    @Autowired
    private VersesRepository versesRepository;

    @Autowired
    private ContactsRepository contactsRepository;

    @Autowired
    private BibleApiService bibleApiService;

    @Autowired
    private ActionLogRepository actionLogRepository;

    @Value("${twilio.phone-number}")
    private String twilioPhoneNumber;

    @GetMapping("/" + Views.admin)
    public String admin() {
        return Views.admin;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder){
        binder.registerCustomEditor(Date.class,
                new CustomDateEditor(new SimpleDateFormat("MM/dd/yyyy"), true, 10));
    }

    @PostMapping("/" + Views.addVerse)
    public RedirectView addContact(@Valid Verse verse, BindingResult result, RedirectAttributes redirectAttributes) {

        if(result.hasErrors()) {
            log.error(result.getAllErrors().toString());
            return new RedirectView(Views.error);
        }

        Integer verseId = verse.getId();
        if(verseId == null || verseId == 0){
            // this is verse add
            versesRepository.save(verse);
            redirectAttributes.addFlashAttribute("sectionId", "add_verse");
            redirectAttributes.addFlashAttribute("addVerseSuccessMessage", "Verse was added!");
        }
        else{
            // this is verse edit
            versesRepository.save(verse);
            redirectAttributes.addFlashAttribute("sectionId", "view_verses");
            redirectAttributes.addFlashAttribute("verseActionSuccessMessage", "Verse was edited successfully!");
        }

        return new RedirectView(Views.admin);
    }

    @GetMapping(value = "/admin/verse/{id}/edit")
    public RedirectView editVerse(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Verse> verse = versesRepository.findById(id);

        // model.addAttribute("verse", verse.get());
        redirectAttributes.addFlashAttribute("verse", verse.get());

        RedirectView redirect = new RedirectView("/" + Views.admin);
        redirect.setExposeModelAttributes(false);
        return redirect;
    }

    @PostMapping(value = "/admin/lookup_verse")
    public RedirectView lookupVerse(@RequestParam("book_name") String bookName,
                                    @RequestParam("chapter_number") Integer chapterNumber,
                                    @RequestParam("verse_number") Integer verseNumber, RedirectAttributes redirectAttributes) throws IOException {
        Verse verse = bibleApiService.getBibleVerse(bookName, chapterNumber, verseNumber);
        redirectAttributes.addFlashAttribute("verse", verse);
        RedirectView redirect = new RedirectView("/" + Views.admin);
        redirect.setExposeModelAttributes(false);
        return redirect;
    }

    @GetMapping("/admin/verse/{id}/delete")
    public RedirectView deleteVerse(@PathVariable("id") Integer id,
                                    RedirectAttributes redirectAttributes) {

        versesRepository.deleteById(id);

        redirectAttributes.addFlashAttribute("sectionId", "view_verses");
        redirectAttributes.addFlashAttribute("verseActionSuccessMessage", "Verse was deleted!");

        RedirectView redirect = new RedirectView("/" + Views.admin);
        redirect.setExposeModelAttributes(false);
        return redirect;
    }

    @ModelAttribute("verse")
    private Verse getVerse(){
        return new Verse();
    }

    @ModelAttribute("verses")
    private List<Verse> getVerses(){
        return versesRepository.findAll();
    }

    @ModelAttribute("contacts")
    private List<Contact> getContacts(){
        return contactsRepository.findAll();
    }

    @ModelAttribute("actionlogs")
    private List<ActionLog> getActionLogs(){
        return actionLogRepository.findAll();
    }

    @ModelAttribute("twilioPhoneNumber")
    private String getTwilioPhoneNumber(){
        return twilioPhoneNumber;
    }

}
