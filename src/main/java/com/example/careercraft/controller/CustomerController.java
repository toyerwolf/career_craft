package com.example.careercraft.controller;

import com.example.careercraft.dto.CustomerInfo;
import com.example.careercraft.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class CustomerController {

    private final AuthService authService;

    @GetMapping("/customerInfo")
    public ResponseEntity<CustomerInfo> getCustomerDetails(@RequestHeader("Authorization") String authHeader) {
        CustomerInfo customerDto = authService.getCustomerDetailsFromToken(authHeader);
        return ResponseEntity.ok(customerDto);
    }
}
