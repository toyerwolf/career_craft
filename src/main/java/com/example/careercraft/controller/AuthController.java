package com.example.careercraft.controller;

import com.example.careercraft.dto.JwtResponse;
import com.example.careercraft.dto.LoginDto;
import com.example.careercraft.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginDto loginDto) {
        JwtResponse jwtResponse = authService.login(loginDto);
        return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<JwtResponse> refreshAccessToken(@RequestHeader("Authorization") String authorizationHeader) {
        String oldToken = authorizationHeader.substring("Bearer ".length());
        JwtResponse jwtResponse = authService.refreshAccessTokenAndGenerateNewToken(oldToken);
        return ResponseEntity.ok(jwtResponse);
    }

//    @GetMapping("/customerId")
//    public ResponseEntity<Long> getCustomerId(@RequestHeader("Authorization") String authHeader) {
//        Long customerId = authService.getCustomerIdFromToken(authHeader);
//        return ResponseEntity.ok(customerId);
//    }



}