package com.sen4ik.vfb.controllers;

import com.github.mkopylec.recaptcha.validation.RecaptchaValidator;
import com.github.mkopylec.recaptcha.validation.ValidationResult;
import com.sen4ik.vfb.constants.Constants;
import com.sen4ik.vfb.constants.Views;
import com.sen4ik.vfb.entities.Contact;
import com.sen4ik.vfb.entities.Verse;
import com.sen4ik.vfb.repositories.ContactsRepository;
import com.sen4ik.vfb.repositories.VersesRepository;
import com.sen4ik.vfb.services.CaptchaService;
import com.sen4ik.vfb.services.ContactsService;
import com.sen4ik.vfb.services.EmailServiceImpl;
import com.sen4ik.vfb.services.TwilioService;
import com.twilio.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;
import java.util.Optional;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Controller
@Slf4j
public class HomeController {

    @Autowired
    private VersesRepository versesRepository;

    @Autowired
    private ContactsRepository contactsRepository;

    @Autowired
    private EmailServiceImpl emailService;

    @Autowired
    private ContactsService contactsService;

    @Autowired
    private TwilioService twilioService;

    @Autowired
    private RecaptchaValidator recaptchaValidator;

    @Autowired
    private CaptchaService captchaService;

    @Value("${twilio.enabled}")
    private Boolean twilioEnabled;

    @Value("${twilio.phone-number}")
    private String twilioPhoneNumber;

    @GetMapping("/")
    public String main() {
        return "redirect:/" + Views.index;
    }

    @GetMapping("/" + Views.index)
    public String home() {
        return Views.index;
    }

    @PostMapping("/" + Views.index)
    public RedirectView addContact(@Valid Contact contact, Model model, BindingResult result, ModelAndView modelAndView, RedirectAttributes redirectAttributes, HttpServletRequest request) {

        if(result.hasErrors()) {
            return new RedirectView(Views.admin);
        }

        redirectAttributes.addFlashAttribute("sectionId", "sign_up");

        ValidationResult validationResult = recaptchaValidator.validate(request);
        if (validationResult.isSuccess()) {
            log.info("reCaptcha success");

            String phoneNumber = contact.getPhoneNumber();
            String sanitizedPhone = contactsService.sanitizePhoneNumber(phoneNumber);

            if(!twilioService.isPhoneNumberValid("+1" + sanitizedPhone)){
                redirectAttributes.addFlashAttribute("addContactErrorMessage", phoneNumber +
                        " is not a valid mobile phone number! Please provide valid US or Canada phone number.");
                return new RedirectView(Views.index);
            }

            Optional<Contact> existingContact = contactsRepository.findByPhoneNumber(sanitizedPhone);
            if (existingContact.isPresent()){

                // What if a phone is already in the database and is subscribed?
                if(existingContact.get().getSubscriptionConfirmed() == 1){
                    redirectAttributes.addFlashAttribute("addContactErrorMessage", "It looks like " +
                            "phone number " + phoneNumber + " is already in our subscription list!</br>If you are " +
                            "having any issues with our services, please contact us using form below.");
                    return new RedirectView(Views.index);
                }

                // If a phone is in the database and subscription is not enabled - restore
                if(existingContact.get().getSubscriptionConfirmed() == 0){
                    redirectAttributes.addFlashAttribute("addContactErrorMessage", "Phone number "
                            + phoneNumber + " is already in our subscription list but subscription was never " +
                            "confirmed. We have resent confirmation SMS.</br>If you are " +
                            "having other issues with our services, please contact us using form below.");
                    return new RedirectView(Views.index);
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

            redirectAttributes.addFlashAttribute("addContactSuccessMessage",
                    "You have been subscribed!</br>We will text you to " + phoneNumber + " for confirmation.");
        }
        else{
            log.info("reCaptcha failed");
            redirectAttributes.addFlashAttribute("addContactErrorMessage", Constants.reCaptchaFailedMessage);
        }

        return new RedirectView(Views.index);
    }

    @PostMapping("/" + Views.unsubscribe)
    public RedirectView unsubscribeContact(@RequestParam(name = "unsubscribe_phone_number") String unsubscribePhoneNumber, RedirectAttributes redirectAttributes, HttpServletRequest request) {

        redirectAttributes.addFlashAttribute("sectionId", "unsubscribe");

        ValidationResult validationResult = recaptchaValidator.validate(request);
        if (validationResult.isSuccess()) {
            log.info("reCaptcha success");

            String sanitizedUnsubscribePhoneNumber = contactsService.sanitizePhoneNumber(unsubscribePhoneNumber);

            Optional<Contact> contact = contactsRepository.findByPhoneNumber(sanitizedUnsubscribePhoneNumber);
            if (!contact.isPresent()){
                redirectAttributes.addFlashAttribute("unsubscribeErrorMessage", "We don't have " +
                        "phone number " + unsubscribePhoneNumber + " in our subscription list!</br>If you are having other issues " +
                        "with our services, please contact us using form below.");
                return new RedirectView(Views.index);
            }

            String message = "We have received request to unsubscribe this number from www.verseformbible.com. Please confirm you want to unsubscribe by replying with the word STOP";
            if(twilioEnabled){
                twilioService.sendSingleMessage("+1" + contact.get().getPhoneNumber(), message);
            }

            redirectAttributes.addFlashAttribute("unsubscribeSuccessMessage", "We sent you a message to " + unsubscribePhoneNumber + ". Please confirm unsubscription from your phone!");
        }
        else{
            log.info("reCaptcha failed");
            redirectAttributes.addFlashAttribute("unsubscribeErrorMessage", Constants.reCaptchaFailedMessage);
        }

        return new RedirectView(Views.index);
    }

    @PostMapping("/" + Views.contactMe)
    public RedirectView contactMe(
            @RequestParam(name = "cm_contact_name") String cmContactName,
            @RequestParam(name = "cm_email") String cmEmail,
            @RequestParam(name = "cm_phone_number") String cmPhoneNumber,
            @RequestParam(name = "cm_message") String cmMessage,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request) {

        redirectAttributes.addFlashAttribute("sectionId", "contact_me");

        ValidationResult validationResult = recaptchaValidator.validate(request);
        if (validationResult.isSuccess()) {
            log.info("reCaptcha success");

            try{
                StringBuilder emailBody = new StringBuilder("Hi " + "\n\n");
                emailBody.append("Name: " + cmContactName + "\n");
                emailBody.append("Email: " + cmEmail + "\n");
                emailBody.append("Phone number: " + cmPhoneNumber + "\n");
                emailBody.append("Message: " + cmMessage + "\n\n");
                emailBody.append("VerseFromBible.com");
                emailService.sendEmail("VerseFromBible Contact Form Message", emailBody.toString());

                redirectAttributes.addFlashAttribute("contactMeSuccessMessage", "We have received your message and will get back to you as soon as we can.");

                return new RedirectView(Views.index);
            }
            catch (Exception e){
                redirectAttributes.addFlashAttribute("contactMeErrorMessage", "Something failed. We apologize for the inconvenience.</br>Please email us at info@sen4ik.info.");
            }
        }
        else{
            redirectAttributes.addFlashAttribute("contactMeErrorMessage", Constants.reCaptchaFailedMessage);
        }

        return new RedirectView(Views.index);
    }

    @ModelAttribute("reCaptchaSiteKey")
    private String getReCaptchaSiteKey(){
        return captchaService.getReCaptchaSite();
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
