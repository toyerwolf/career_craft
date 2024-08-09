package com.example.careercraft.service;

import com.example.careercraft.req.UserRegistrationReq;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@RequiredArgsConstructor

public abstract class AbstractRegistrationService implements RegistrationService {


//    @Autowired
//    private JavaMailSender mailSender;

    @Override
    public abstract void register(UserRegistrationReq request);

//    protected void sendConfirmationEmail(String email) {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(email);
//        message.setSubject("Registration Confirmation");
//        message.setText("Thank you for registering. Please confirm your email.");
//        message.setFrom("huseynmamedov472@gmail.com");
//
//        mailSender.send(message);
//    }
}