package com.example.careercraft.controller;

import com.example.careercraft.dto.CustomerInfo;
import com.example.careercraft.req.CustomerUpdateRequest;
import com.example.careercraft.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/customers")
public class CustomerController {

    private final AuthService authService;

    @Secured("USER")
    @GetMapping("info")
    @CrossOrigin(origins = "https://career-craft.netlify.app")
    public ResponseEntity<CustomerInfo> getCustomerDetails(@RequestHeader("Authorization") String authHeader) {
        CustomerInfo customerDto = authService.getCustomerDetailsFromToken(authHeader);
        return ResponseEntity.ok(customerDto);
    }


    @PutMapping("/update")
    @CrossOrigin(origins = "https://career-craft.netlify.app")
    public ResponseEntity<CustomerInfo> updateCustomerDetails(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody @Valid CustomerUpdateRequest updateRequest) {
        CustomerInfo updatedCustomerInfo = authService.updateCustomerDetails(authHeader, updateRequest);
        return ResponseEntity.ok(updatedCustomerInfo);
    }
}
