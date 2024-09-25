package com.example.careercraft.service.impl;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@AllArgsConstructor
public class PayPalService {

    private final APIContext apiContext;



    public void createPaymentWithCard(
            Double total,
            String currency,
            String cardType,
            String cardNumber,
            String expireMonth,
            String expireYear,
            String cvv,
            String firstName,
            String lastName) throws PayPalRESTException {

        Amount amount = new Amount();
        amount.setCurrency(currency);
        amount.setTotal(String.format("%.2f", total));

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setDescription("Payment description");

        // Добавляем Payee (получатель)
        Payee payee = new Payee();
        payee.setEmail("huseynmamedov472@gmail.com"); // Укажите email вашего sandbox бизнес-аккаунта
        transaction.setPayee(payee);

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        CreditCard creditCard = new CreditCard();
        creditCard.setType(cardType.toLowerCase());
        creditCard.setNumber(cardNumber);
        creditCard.setExpireMonth(Integer.parseInt(expireMonth));
        creditCard.setExpireYear(Integer.parseInt(expireYear));
        creditCard.setCvv2(cvv);
        creditCard.setFirstName(firstName);
        creditCard.setLastName(lastName);

        FundingInstrument fundingInstrument = new FundingInstrument();
        fundingInstrument.setCreditCard(creditCard);

        Payer payer = new Payer();
        payer.setPaymentMethod("credit_card");
        payer.setFundingInstruments(List.of(fundingInstrument));

        Payment payment = new Payment();
        payment.setIntent("sale");
        payment.setPayer(payer);
        payment.setTransactions(transactions);

        Payment createdPayment = payment.create(apiContext);
        System.out.println("Payment created successfully with ID: " + createdPayment.getId());
    }
}
