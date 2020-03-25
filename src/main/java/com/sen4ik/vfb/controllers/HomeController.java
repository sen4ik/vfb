package com.sen4ik.vfb.controllers;

import com.sen4ik.vfb.entities.Contact;
import com.sen4ik.vfb.entities.Verse;
import com.sen4ik.vfb.enums.Views;
import com.sen4ik.vfb.repositories.ContactsRepository;
import com.sen4ik.vfb.repositories.VersesRepository;
import com.sen4ik.vfb.services.ContactsService;
import com.sen4ik.vfb.services.EmailServiceImpl;
import com.sen4ik.vfb.services.TelerivetService;
import com.sen4ik.vfb.services.TwilioService;
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
    private TelerivetService telerivetService;

    @Autowired
    private EmailServiceImpl emailService;

    @Autowired
    private ContactsService contactsService;

    @Value("${telerivet.enabled}")
    private Boolean telerivetEnabled;

    @Autowired
    private TwilioService twilioService;

    @Value("${twilio.enabled}")
    private Boolean twilioEnabled;

    @GetMapping("/")
    public String main() {
        return "redirect:/" + Views.home.value;
    }

    @GetMapping("/index")
    public String home() {
        return Views.home.value;
    }

    @PostMapping("/index")
    public RedirectView addContact(@Valid Contact contact, Model model, BindingResult result, ModelAndView modelAndView, RedirectAttributes redirectAttributes) {

        if(result.hasErrors()) {
            return new RedirectView(Views.admin.value);
        }

        redirectAttributes.addFlashAttribute("sectionId", "sign_up");

        String phoneNumber = contact.getPhoneNumber();
        String sanitizedPhone = contactsService.sanitizePhoneNumber(phoneNumber);

        Optional<Contact> existingContact = contactsRepository.findByPhoneNumber(sanitizedPhone);
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

        String message = "It looks like you have subscribed to VerseFromBible.com. If that is correct, please reply YES.";
        if(twilioEnabled){
            twilioService.sendSingleMessage("+1" + sanitizedPhone, message);
        }

        if(telerivetEnabled){
            try {
                telerivetService.sendSingleMessage("+1" + sanitizedPhone, message);
            } catch (IOException e) {
                // TODO:
            }
        }

        redirectAttributes.addFlashAttribute("addContactSuccessMessage", "You have been subscribed! We will text you to " + phoneNumber + " for confirmation.");

        return new RedirectView(Views.home.value);
    }

    @PostMapping("/unsubscribe")
    public RedirectView unsubscribeContact(@RequestParam(name = "unsubscribe_phone_number") String unsubscribePhoneNumber, RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("sectionId", "unsubscribe");

        String sanitizedUnsubscribePhoneNumber = contactsService.sanitizePhoneNumber(unsubscribePhoneNumber);

        Optional<Contact> contact = contactsRepository.findByPhoneNumber(sanitizedUnsubscribePhoneNumber);
        if (!contact.isPresent()){
            redirectAttributes.addFlashAttribute("unsubscribeErrorMessage", "We don't have " +
                    "phone number " + unsubscribePhoneNumber + " in our subscription list!</br>If you are having other issues " +
                    "with our services, please contact us using form below.");
            return new RedirectView(Views.home.value);
        }

        String message = "We have received request to unsubscribe this number from www.verseformbible.com. Please confirm you want to unsubscribe by replying with the word STOP";
        if(twilioEnabled){
            twilioService.sendSingleMessage("+1" + contact.get().getPhoneNumber(), message);
        }

        redirectAttributes.addFlashAttribute("unsubscribeSuccessMessage", "We sent you a message to " + unsubscribePhoneNumber + ". Please confirm unsubscription from your phone!");
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
            StringBuilder emailBody = new StringBuilder("Hi " + "\n\n");
            emailBody.append("Name: " + cmContactName + "\n");
            emailBody.append("Email: " + cmEmail + "\n");
            emailBody.append("Phone number: " + cmPhoneNumber + "\n");
            emailBody.append("Message: " + cmMessage + "\n\n");
            emailBody.append("VerseFromBible.com");
            emailService.sendEmail("VerseFromBible Contact Form Message", emailBody.toString());

            redirectAttributes.addFlashAttribute("contactMeSuccessMessage", "We have received your message and will get back to you as soon as we can.");

            return new RedirectView(Views.home.value);
        }
        catch (Exception e){
            redirectAttributes.addFlashAttribute("contactMeErrorMessage", "Something failed. We apologize for the inconvenience.</br>Please email us at info@sen4ik.info.");
        }

        return new RedirectView(Views.home.value);
    }

    @ModelAttribute("contact")
    private Contact getContact(){
        return new Contact();
    }

    @ModelAttribute("verse")
    private Verse getVerse(){
        Optional<Verse> verse = versesRepository.findByDate(new Date());
        if (!verse.isPresent()){
            Verse ve = new Verse();
            ve.setEnEsv("For God so loved the world, that he gave his only Son, that whoever believes in him should not perish but have eternal life.");
            ve.setEnVerseLocation("John 3:16");
            return ve;
        }
        else{
            return verse.get();
        }
    }

}
