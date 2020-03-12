package com.sen4ik.vfb.controllers;

import com.sen4ik.vfb.entities.ContactsEntity;
import com.sen4ik.vfb.entities.VersesEntity;
import com.sen4ik.vfb.enums.Views;
import com.sen4ik.vfb.repositories.ContactsRepository;
import com.sen4ik.vfb.repositories.VersesRepository;
import com.sen4ik.vfb.services.EmailServiceImpl;
import com.sen4ik.vfb.services.TelerivetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Date;
import java.util.Optional;

@Controller
@Slf4j
public class HomeController {

    // https://www.baeldung.com/spring-boot-crud-thymeleaf

    @Autowired
    private VersesRepository versesRepository;

    @Autowired
    private ContactsRepository contactsRepository;

    @Autowired
    TelerivetService telerivetService;

    @Autowired
    EmailServiceImpl emailService;

    @Value("${telerivet.enabled}")
    private Boolean telerivetEnabled;

    @GetMapping("/")
    public String main() {
        return "redirect:/" + Views.home.value;
    }

    @GetMapping("/home")
    public String home() {

        /*
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
        */

        return Views.home.value; // view
    }

    @PostMapping("/home")
    public RedirectView addContact(@Valid ContactsEntity contact, Model model, BindingResult result, ModelAndView modelAndView, RedirectAttributes redirectAttributes) {

        if(result.hasErrors()) {
            // return "error";
            return new RedirectView(Views.admin.value);
        }

        // model.addAttribute("addContactSuccessMessage", "You have been subscribed! We will text you to " + phoneNumber + " for confirmation.");
        //return "home";

        // modelAndView.clear();
        // modelAndView.addObject("addContactSuccessMessage", "You have been subscribed! We will text you to " + phoneNumber + " for confirmation.");
        // modelAndView.setViewName("home");
        // return modelAndView;

        // return "redirect:/home";

        redirectAttributes.addFlashAttribute("sectionId", "sign_up");

        String phoneNumber = contact.getPhoneNumber();
        String sanitizedPhone = sanitizePhoneNumber(phoneNumber);

        Optional<ContactsEntity> existingContact = contactsRepository.findByPhoneNumber(sanitizedPhone);
        if (existingContact.isPresent()){

            // What if a phone is already in the database and is subscribed?
            if(existingContact.get().getSubscriptionConfirmed() == 1){
                redirectAttributes.addFlashAttribute("addContactErrorMessage", "It looks like " +
                        "phone number " + phoneNumber + " is already in our subscription list!</br>If you are " +
                        "having any issues with our services, please contact us using form below.");
                return new RedirectView(Views.home.value);
            }

            // If a phone is in the database and subscription is not enabled - restore
            if(existingContact.get().getSubscriptionConfirmed() == 0){
                redirectAttributes.addFlashAttribute("addContactErrorMessage", "Phone number "
                        + phoneNumber + " is already in our subscription list but subscription was never " +
                        "confirmed. We have resent confirmation SMS.</br>If you are " +
                        "having other issues with our services, please contact us using form below.");
                return new RedirectView(Views.home.value);
            }

        }

        Double selectedSendTime = contact.getSelectedSendTime();
        Double selectedSendTimePacific = selectedSendTime;
        String selectedTimeZone = contact.getSelectedTimeZone();

        switch(selectedTimeZone) {
            case "mountain":
                selectedSendTimePacific = selectedSendTime + 1;
                break;
            case "central":
                selectedSendTimePacific = selectedSendTime + 2;
                break;
            case "eastern":
                selectedSendTimePacific = selectedSendTime + 3;
                break;
            case "alaska":
                selectedSendTimePacific = selectedSendTime - 1;
                break;
            case "hawaii":
                selectedSendTimePacific = selectedSendTime - 3;
                break;
        }

        contact.setSelectedSendTimePacific(selectedSendTimePacific);
        contact.setPhoneNumber(sanitizedPhone);
        contactsRepository.save(contact);

        if(telerivetEnabled){
            try {
                telerivetService.sendSingleMessage("+1" + phoneNumber, "It looks like you have subscribed to VerseFromBible.com. If that is correct, please reply YES.");
            } catch (IOException e) {
                // TODO:
            }
        }

        redirectAttributes.addFlashAttribute("addContactSuccessMessage", "You have been subscribed! We will text you to " + phoneNumber + " for confirmation.");

        return new RedirectView(Views.home.value);
    }

    private String sanitizePhoneNumber(String pn){
        return pn.replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("-", "").trim();
    }

    @PostMapping("/unsubscribe")
    public RedirectView unsubscribeContact(@RequestParam(name = "unsubscribe_phone_number") String unsubscribePhoneNumber, RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("sectionId", "unsubscribe");

        String sanitizedUnsubscribePhoneNumber = sanitizePhoneNumber(unsubscribePhoneNumber);

        Optional<ContactsEntity> contact = contactsRepository.findByPhoneNumber(sanitizedUnsubscribePhoneNumber);
        if (!contact.isPresent()){
            redirectAttributes.addFlashAttribute("unsubscribeErrorMessage", "We don't have " +
                    "phone number " + unsubscribePhoneNumber + " in our subscription list!</br>If you are having other issues " +
                    "with our services, please contact us using form below.");
            return new RedirectView(Views.home.value);
        }

        contactsRepository.delete(contact.get());

        redirectAttributes.addFlashAttribute("unsubscribeSuccessMessage", "Your phone number " + unsubscribePhoneNumber + " have been removed from our subscription list!");
        return new RedirectView(Views.home.value);
    }

    @PostMapping("/contact_me")
    public RedirectView contactMe(
            @RequestParam(name = "cm_contact_name") String cmContactName,
            @RequestParam(name = "cm_email") String cmEmail,
            @RequestParam(name = "cm_phone_number") String cmPhoneNumber,
            @RequestParam(name = "cm_message") String cmMessage,
            RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("sectionId", "contact_me");

        try{
            StringBuilder emailBody = new StringBuilder("Hi ");
            emailBody.append("Name: " + cmContactName);
            emailBody.append("Phone number: " + cmPhoneNumber);
            emailBody.append("Message: " + cmMessage);
            emailService.sendEmail(cmEmail, "VerseFromBible Contact Form Message", emailBody.toString());

            redirectAttributes.addFlashAttribute("contactMeSuccessMessage", "We have received your message and will get back to you as soon as we can.");

            return new RedirectView(Views.home.value);
        }
        catch (Exception e){
            redirectAttributes.addFlashAttribute("contactMeErrorMessage", "Something failed. We apologize for the inconvenience.</br>Please email us at info@sen4ik.info.");
        }

        return new RedirectView(Views.home.value);
    }

    @ModelAttribute("contact")
    private ContactsEntity getContact(){
        return new ContactsEntity();
    }

    @ModelAttribute("verse")
    private VersesEntity getVerse(){
        Optional<VersesEntity> verse = versesRepository.findByDate(new Date());
        if (!verse.isPresent()){
            VersesEntity ve = new VersesEntity();
            ve.setEnEsv("For God so loved the world, that he gave his only Son, that whoever believes in him should not perish but have eternal life.");
            ve.setEnVerseLocation("John 3:16");
            return ve;
        }
        else{
            return verse.get();
        }
    }

}
