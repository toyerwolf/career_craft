package com.example.careercraft.service;

import com.example.careercraft.dto.CustomerInfo;
import com.example.careercraft.dto.JwtResponse;
import com.example.careercraft.dto.LoginDto;

public interface AuthService {

    JwtResponse login(LoginDto loginDto);

    public JwtResponse refreshAccessTokenAndGenerateNewToken(String oldToken, String refreshToken);

    public CustomerInfo getCustomerDetailsFromToken(String authHeader);
}
