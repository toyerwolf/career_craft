package com.example.careercraft.controller;

import com.example.careercraft.req.UserRegistrationReq;
import com.example.careercraft.service.RegistrationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("registration")
public class RegistrationController {


    private final RegistrationService registrationService;

    @PostMapping
    @CrossOrigin(origins = "https://career-craft.netlify.app")
    public ResponseEntity<String> registerUser(@RequestBody UserRegistrationReq request) {
        registrationService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("Registration successful. Please check your email for confirmation.");
    }
}