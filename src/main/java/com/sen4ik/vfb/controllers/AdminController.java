package com.sen4ik.vfb.controllers;

import com.sen4ik.vfb.entities.ContactsEntity;
import com.sen4ik.vfb.entities.VersesEntity;
import com.sen4ik.vfb.repositories.ContactsRepository;
import com.sen4ik.vfb.repositories.VersesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
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

        versesRepository.save(verse);

        redirectAttributes.addFlashAttribute("sectionId", "add_verse");
        redirectAttributes.addFlashAttribute("addVerseSuccessMessage", "Verse was added!");

        return new RedirectView("admin");
    }

    @GetMapping(value = "/admin/verse/{id}/edit")
    public RedirectView editVerse(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        Optional<VersesEntity> verse = versesRepository.findById(id);

        // model.addAttribute("verse", verse.get());
        redirectAttributes.addFlashAttribute("verse", verse.get());

        RedirectView redirect = new RedirectView("/admin");
        redirect.setExposeModelAttributes(false);
        return redirect;
    }

    private RedirectView redirectTo_exposeModelAttributesDisabled(String url){
        RedirectView redirect = new RedirectView(url);
        redirect.setExposeModelAttributes(false);
        return redirect;
    }

    // @RequestMapping(value = "/admin/verse/{id}/delete", method = RequestMethod.GET)
    @GetMapping("/admin/verse/{id}/delete")
    public RedirectView deleteVerse(@PathVariable("id") Integer id,
                                    RedirectAttributes redirectAttributes) {

        versesRepository.deleteById(id);

        redirectAttributes.addFlashAttribute("sectionId", "view_verses");
        redirectAttributes.addFlashAttribute("deleteVerseSuccessMessage", "Verse was deleted!");

        RedirectView redirect = new RedirectView("/admin");
        redirect.setExposeModelAttributes(false);
        return redirect;
    }

    @ModelAttribute("verse")
    private VersesEntity getVerse(){
        return new VersesEntity();
    }

    @ModelAttribute("verses")
    private List<VersesEntity> getVerses(){
        return versesRepository.findAll();
    }

    @ModelAttribute("contacts")
    private List<ContactsEntity> getContacts(){
        return contactsRepository.findAll();
    }

}
