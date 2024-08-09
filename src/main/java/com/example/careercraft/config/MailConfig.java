//package com.example.careercraft.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.JavaMailSenderImpl;
//
//import java.util.Properties;
//
//@Configuration
//public class MailConfig {
//
//    @Bean
//    public JavaMailSender javaMailSender() {
//        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
//
//        // Устанавливаем параметры подключения
//        mailSender.setHost("smtp.gmail.com");
//        mailSender.setPort(587);
//        mailSender.setUsername("huseynmamedov472@gmail.com");
//        mailSender.setPassword("yunhntrkwusgjemw"); // Убедитесь, что вы не используете реальные пароли в исходном коде
//
//        // Настраиваем свойства
//        Properties props = mailSender.getJavaMailProperties();
//        props.put("mail.transport.protocol", "smtp");
//        props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.starttls.enable", "true");
//        props.put("mail.smtp.starttls.required", "true");
//        props.put("mail.smtp.connection-timeout", "5000");
//        props.put("mail.smtp.timeout", "5000");
//        props.put("mail.smtp.write-timeout", "5000");
//
//        return mailSender;
//    }
//}