package com.example.careercraft.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @GetMapping("/payment-success")
    public String paymentSuccess() {
        // Логика для обработки успешного платежа
        return "Payment was successful!";
    }

    @GetMapping("/payment-cancel")
    public String paymentCancel() {
        // Логика для обработки отмены платежа
        return "Payment was canceled!";
    }
}