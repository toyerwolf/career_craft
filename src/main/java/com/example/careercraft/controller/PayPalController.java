package com.example.careercraft.controller;

import com.example.careercraft.service.impl.PayPalService;
import com.paypal.base.rest.PayPalRESTException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("paypal")
public class PayPalController {

    private final PayPalService payPalService;


    @GetMapping("/test-payment")
    public String testPayment() {
        try {
            // Используйте тестовые данные
            String cardNumber = "4032037878354238"; // Тестовый номер карты
            String expireMonth = "06"; // Месяц истечения
            String expireYear = "2026"; // Год истечения
            String cvv = "715"; // CVV
            String firstName = "John";
            String lastName = "Doe";
            Double total = 5.00;
            String currency = "USD";
            String cardType = "visa"; // Добавьте тип карты

            payPalService.createPaymentWithCard(
                    total, currency, cardType, cardNumber, expireMonth, expireYear, cvv, firstName, lastName
            );

            return "Payment created successfully!";
        } catch (PayPalRESTException e) {
            return "Payment creation failed: " + e.getMessage() + ". Debug ID: " + e.getDetails().getDebugId();
        }
    }
}