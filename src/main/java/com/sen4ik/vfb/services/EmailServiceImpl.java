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

    @Value("${email.mine}")
    private String to;

    @Async
    public void sendEmail(String from, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        message.setFrom(from);
        emailSender.send(message);
    }

    @Async
    public void sendEmail(SimpleMailMessage email) {
        emailSender.send(email);
    }
}
