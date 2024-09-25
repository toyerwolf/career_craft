package com.example.careercraft.controller;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.braintreegateway.TransactionRequest;
import com.example.careercraft.req.PaymentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private BraintreeGateway braintreeGateway;

    @PostMapping("/process-payment")
    public ResponseEntity<String> processPayment(@RequestBody PaymentRequest paymentRequest) {
        TransactionRequest request = new TransactionRequest()
                .amount(paymentRequest.getAmount())
                .paymentMethodNonce(paymentRequest.getPaymentMethodNonce())
                .options()
                .submitForSettlement(true)
                .done();

        Result<Transaction> result = braintreeGateway.transaction().sale(request);

        if (result.isSuccess()) {
            return ResponseEntity.ok("Payment successful! Transaction ID: " + result.getTarget().getId());
        } else {
            // Выводим сообщение об ошибке, если платеж не прошел
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment failed: " + result.getMessage());
        }
    }

    @PostMapping("/client-token")
    public ResponseEntity<String> generateClientToken() {
        String clientToken = braintreeGateway.clientToken().generate();
        return ResponseEntity.ok(clientToken);
    }
}