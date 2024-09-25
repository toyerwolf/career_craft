package com.example.careercraft.req;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PaymentRequest {
    private BigDecimal amount; // Сумма платежа
    private String paymentMethodNonce;

}