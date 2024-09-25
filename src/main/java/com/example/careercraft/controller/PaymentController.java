package com.example.careercraft.controller;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.braintreegateway.TransactionRequest;
import com.example.careercraft.req.PaymentRequest;
import com.example.careercraft.response.PaymentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private BraintreeGateway braintreeGateway;

    @PostMapping("/process-payment")
    @Secured("USER")
    public ResponseEntity<PaymentResponse> processPayment(@RequestBody PaymentRequest paymentRequest) {
        TransactionRequest request = new TransactionRequest()
                .amount(paymentRequest.getAmount())
                .paymentMethodNonce(paymentRequest.getPaymentMethodNonce())
                .options()
                .submitForSettlement(true)
                .done();

        Result<Transaction> result = braintreeGateway.transaction().sale(request);

        if (result.isSuccess()) {
            // Возвращаем успешный ответ с информацией о транзакции
            PaymentResponse response = new PaymentResponse("Payment successful!", result.getTarget().getId());
            return ResponseEntity.ok(response);
        } else {
            // Возвращаем сообщение об ошибке в формате JSON
            PaymentResponse response = new PaymentResponse("Payment failed: " + result.getMessage(), null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/client-token")
    public ResponseEntity<String> generateClientToken() {
        // Добавьте логирование перед генерацией токена
        System.out.println("Merchant ID: " + braintreeGateway.getConfiguration().getClientId());
        String clientToken = braintreeGateway.clientToken().generate();
        return ResponseEntity.ok(clientToken);
    }
}