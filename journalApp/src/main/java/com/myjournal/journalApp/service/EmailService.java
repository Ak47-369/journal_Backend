package com.myjournal.journalApp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {
    private final JavaMailSender mailSender;
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        log.info("Sending email to: {}", to);
        try{
            mailSender.send(message);
            log.info("Email sent to: {}", to);
        }catch (Exception e){
            log.error("Error while sending mail : {}", e.getMessage());
        }
    }

}
