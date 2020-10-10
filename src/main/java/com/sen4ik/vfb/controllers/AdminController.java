package com.sen4ik.vfb.controllers;

import com.sen4ik.vfb.constants.Constants;
import com.sen4ik.vfb.constants.Views;
import com.sen4ik.vfb.entities.ActionLog;
import com.sen4ik.vfb.entities.Contact;
import com.sen4ik.vfb.entities.Verse;
import com.sen4ik.vfb.repositories.ActionLogRepository;
import com.sen4ik.vfb.repositories.ContactsRepository;
import com.sen4ik.vfb.repositories.VersesRepository;
import com.sen4ik.vfb.services.BibleApiService;
import com.sen4ik.vfb.services.TwilioService;
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
import java.util.Calendar;
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

    @Autowired
    private TwilioService twilioService;

    @Value("${twilio.enabled}")
    private Boolean twilioEnabled;

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
    public RedirectView addVerse(@Valid Verse verse, BindingResult result, RedirectAttributes redirectAttributes) {

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
                                    @RequestParam("verse_number") Integer fromVerse,
                                    @RequestParam("verse_number_to") Integer toVerse,
                                    RedirectAttributes redirectAttributes) throws IOException {

        if(chapterNumber != null && chapterNumber <= 0) {
            redirectAttributes.addFlashAttribute("addVerseErrorMessage", "Lookup Error! The Chapter value can't be equal or less than zero.");
        }
        //else if((fromVerse != null || toVerse != null) && (toVerse <= 0 || fromVerse <= 0)){
        else if((fromVerse != null && fromVerse <= 0) || (toVerse != null && toVerse <= 0)){
            redirectAttributes.addFlashAttribute("addVerseErrorMessage", "Lookup Error! The From and To values can't equal or less than zero.");
        }
        else if(toVerse != null && toVerse < fromVerse){
            redirectAttributes.addFlashAttribute("addVerseErrorMessage", "Lookup Error! The To value can't be less than From value.");
        }
        else{
            Verse verse = bibleApiService.getBibleVerse(bookName, chapterNumber, fromVerse, toVerse);
            if(verse == null){
                redirectAttributes.addFlashAttribute("addVerseErrorMessage", "Error occurred during lookup!");
            }
            else{
                redirectAttributes.addFlashAttribute("verse", verse);
                verse.setDate(getDateForTheNextVerse());
            }
        }

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

    @GetMapping("/admin/contact/{id}/delete")
    public RedirectView deleteContact(@PathVariable("id") Integer id,
                                    RedirectAttributes redirectAttributes) {

        contactsRepository.deleteById(id);

        redirectAttributes.addFlashAttribute("sectionId", "view_contacts");
        redirectAttributes.addFlashAttribute("verseActionSuccessMessage", "Contact was deleted!");

        RedirectView redirect = new RedirectView("/" + Views.admin);
        redirect.setExposeModelAttributes(false);
        return redirect;
    }

    @GetMapping("/admin/contact/{id}/resendConfirmation")
    public RedirectView resendConfirmation(@PathVariable("id") Integer id,
                                    RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("sectionId", "view_contacts");

        Optional<Contact> optionalContact = contactsRepository.findById(id);
        if (!optionalContact.isPresent()){
            redirectAttributes.addFlashAttribute("contactActionErrorMessage", "No contact found with ID " + id + ".");
        }
        else{
            if(twilioEnabled){
                twilioService.sendSingleMessage(optionalContact.get().getPhoneNumber(), Constants.confirmSubscriptionMessage);
                redirectAttributes.addFlashAttribute("contactActionSuccessMessage", "Confirmation message resent.");
            }
        }

        RedirectView redirect = new RedirectView("/" + Views.admin);
        redirect.setExposeModelAttributes(false);
        return redirect;
    }

    @GetMapping("/admin/contact/{id}/resendVerse")
    public RedirectView resendVerse(@PathVariable("id") Integer id,
                                      RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("sectionId", "view_contacts");

        Optional<Contact> optionalContact = contactsRepository.findById(id);
        if (!optionalContact.isPresent()){
            redirectAttributes.addFlashAttribute("contactActionErrorMessage", "No contact found with ID " + id + ".");
        }
        else{
            Optional<Verse> optionalVerse = versesRepository.findByDate(new Date());
            if (!optionalVerse.isPresent()){
                redirectAttributes.addFlashAttribute("contactActionErrorMessage", "No verse found for today's date.");
            }
            else{
                String bibleTranslation = optionalContact.get().getBibleTranslation();
                String verseToSend = null;

                if(bibleTranslation.equals("en_esv")){
                    verseToSend = optionalVerse.get().getEnEsv() + " " + optionalVerse.get().getEnVerseLocation();
                }
                else if(bibleTranslation.equals("en_niv")){
                    verseToSend = optionalVerse.get().getEnNiv() + " " + optionalVerse.get().getEnVerseLocation();
                }
                else if(bibleTranslation.equals("en_kjv")){
                    verseToSend = optionalVerse.get().getEnKjv() + " " + optionalVerse.get().getEnVerseLocation();
                }
                else if(bibleTranslation.equals("ru_synodal")){
                    verseToSend = optionalVerse.get().getRuSynodal() + " " + optionalVerse.get().getRuVerseLocation();
                }

                if(verseToSend != null){
                    if(twilioEnabled) {
                        twilioService.sendSingleMessage(optionalContact.get().getPhoneNumber(), verseToSend);
                    }
                    redirectAttributes.addFlashAttribute("contactActionSuccessMessage", "Verse resent successfully.");
                }
                else{
                    redirectAttributes.addFlashAttribute("contactActionErrorMessage", "Error occurred.");
                }

            }
        }

        RedirectView redirect = new RedirectView("/" + Views.admin);
        redirect.setExposeModelAttributes(false);
        return redirect;
    }

    @ModelAttribute("verse")
    private Verse getVerse(){
        Verse v = new Verse();
        v.setDate(getDateForTheNextVerse());
        return v;
    }

    private Date getDateForTheNextVerse(){
        Verse latestVerse = versesRepository.findTopByOrderByDateDesc();
        Date latestVerseDate = latestVerse.getDate();

        Calendar cal = Calendar.getInstance();
        cal.setTime(latestVerseDate);
        cal.add(Calendar.DATE, 1);
        Date dateForTheNextVerseDt = cal.getTime();

        // SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        // String dateForTheNextVerse = sdf.format(dateForTheNextVerseDt);

        return dateForTheNextVerseDt;
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
        List<ActionLog> actionLogs = actionLogRepository.findAll();
        // return actionLogs.stream().limit(500).collect(Collectors.toList());
        if(actionLogs.size() > 1000){
            return actionLogs.subList(actionLogs.size() - 500, actionLogs.size());
        }
        return actionLogs;
    }

    @ModelAttribute("twilioPhoneNumber")
    private String getTwilioPhoneNumber(){
        return twilioPhoneNumber;
    }

}
