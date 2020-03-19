package com.sen4ik.vfb.controllers;

import com.nulabinc.zxcvbn.Feedback;
import com.nulabinc.zxcvbn.Strength;
import com.nulabinc.zxcvbn.Zxcvbn;
import com.sen4ik.vfb.entities.Role;
import com.sen4ik.vfb.entities.User;
import com.sen4ik.vfb.services.EmailServiceImpl;
import com.sen4ik.vfb.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@Slf4j
public class RegisterController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailServiceImpl emailService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Value("${my.email}")
    private String from;

    @RequestMapping(value="/register", method = RequestMethod.GET)
    public ModelAndView showRegistrationPage(ModelAndView modelAndView, User user){
        modelAndView.addObject("user", user);
        modelAndView.setViewName("register");
        return modelAndView;
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ModelAndView processRegistrationForm(ModelAndView modelAndView, @Valid User user, BindingResult bindingResult, HttpServletRequest request) {

        // Lookup user in database by e-mail
        User userExists = userService.findByEmail(user.getEmail());
        // System.out.println(userExists);

        if (userExists != null) {
            modelAndView.addObject("alreadyRegisteredMessage", "Oops!  There is already a user registered with the email provided.");
            modelAndView.setViewName("register");
            bindingResult.reject("email");
        }

        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("register");
        }
        else { // new user so we create user and send confirmation e-mail

            // Disable user until they click on confirmation link in email
            user.setRole(Role.USER);
            user.setEnabled(false);

            // Generate random 36-character string token for confirmation link
            user.setConfirmationToken(UUID.randomUUID().toString());
            System.out.println("*****************************\n\n\n  " + user.getConfirmationToken() + "\n\n\n******************");
            userService.saveUser(user);

            // TODO:
            String appUrl = request.getScheme() + "://" + request.getServerName();
            log.info(appUrl);

            // TODO: register noreply email and set send from
            SimpleMailMessage registrationEmail = new SimpleMailMessage();
            registrationEmail.setTo(user.getEmail());
            registrationEmail.setSubject("Registration Confirmation");
            registrationEmail.setText("To confirm your e-mail address, please click the link below:\n"
                    + appUrl + "/confirm?token=" + user.getConfirmationToken());
            registrationEmail.setFrom(from);

            emailService.sendEmail(registrationEmail);

            modelAndView.addObject("confirmationMessage", "A confirmation e-mail has been sent to " + user.getEmail());
            modelAndView.setViewName("register");
        }

        return modelAndView;
    }

    @RequestMapping(value="/confirm", method = RequestMethod.GET)
    public ModelAndView confirmRegistration(ModelAndView modelAndView, @RequestParam("token") String token) {

        User user = userService.findByConfirmationToken(token);

        if (user == null) { // No token found in DB
            modelAndView.addObject("invalidToken", "Oops! This is an invalid confirmation link.");
        } else { // Token found
            modelAndView.addObject("confirmationToken", user.getConfirmationToken());
        }

        modelAndView.setViewName("confirm");
        return modelAndView;
    }

    // Process confirmation link
    @RequestMapping(value="/confirm", method = RequestMethod.POST)
    public ModelAndView processRegistration(ModelAndView modelAndView, BindingResult bindingResult, @RequestParam Map<String, String> requestParams, RedirectAttributes redir) {

        modelAndView.setViewName("confirm");

        String password = requestParams.get("password");
        String tokenStr = requestParams.get("token");

        Zxcvbn passwordCheck = new Zxcvbn();

        Strength strength = passwordCheck.measure(password);

        if (strength.getScore() < 3) {
            //modelAndView.addObject("errorMessage", "Your password is too weak. Choose a stronger one.");
            bindingResult.reject("password");
            Feedback feedback = strength.getFeedback();
            String warning = feedback.getWarning();
            String suggestions = feedback.getSuggestions().stream().collect(Collectors.joining(" "));
            redir.addFlashAttribute("errorMessage", "Warning: " + warning + ". </br>We suggest: " + suggestions);
            modelAndView.setViewName("redirect:confirm?token=" + requestParams.get("token"));
            // System.out.println(requestParams.get("token"));
            return modelAndView;
        }

        // Find the user associated with the reset token
        User user = userService.findByConfirmationToken(tokenStr);

        // Set new password
        user.setPassword(bCryptPasswordEncoder.encode(password));

        // Set user to enabled
        user.setEnabled(true);

        // Save user
        userService.saveUser(user);

        modelAndView.addObject("successMessage", "Your password has been set!");
        modelAndView.addObject("user", user);
        modelAndView.setViewName("login");
        return modelAndView;
    }
}
