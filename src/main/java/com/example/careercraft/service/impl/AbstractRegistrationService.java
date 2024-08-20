//package com.example.careercraft.service.impl;
//
//import com.example.careercraft.req.UserRegistrationReq;
//import com.example.careercraft.service.RegistrationService;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//
//public abstract class AbstractRegistrationService implements RegistrationService {
//
//    private final JavaMailSender mailSender;
//
//    public AbstractRegistrationService(JavaMailSender mailSender) {
//        this.mailSender = mailSender;
//    }
//
//    @Override
//    public abstract void register(UserRegistrationReq request);
//
//    protected void sendConfirmationEmail(String email) {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(email);
//        message.setSubject("Registration Confirmation");
//        message.setText("Thank you for registering. Please confirm your email.");
//        message.setFrom("huseynmamedov472@gmail.com");
//
//        mailSender.send(message);
//    }
//}