package com.example.careercraft.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PaymentViewController {

    @GetMapping("/payment-form")
    public String getPaymentForm() {
        // Возвращаем имя HTML-файла без расширения, который находится в папке resources/templates
        return "test";
    }

}