package com.sen4ik.vfb.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@Service
public class EmailServiceImpl {

    @Autowired
    public JavaMailSender emailSender;

    @Value("${spring.mail.username}")
    private String mailEmail;

    @Value("${my.email}")
    private String myEmail;

    @Async
    public void sendEmail(String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(myEmail);
        message.setSubject(subject);
        message.setText(text);
        message.setFrom(mailEmail);
        emailSender.send(message);
    }

    @Async
    public void sendEmail(SimpleMailMessage email) {
        emailSender.send(email);
    }
}
