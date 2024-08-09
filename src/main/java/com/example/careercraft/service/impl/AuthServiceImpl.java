package com.example.careercraft.service.impl;

import com.example.careercraft.dto.CustomerInfo;
import com.example.careercraft.dto.JwtResponse;
import com.example.careercraft.dto.LoginDto;
import com.example.careercraft.entity.Customer;
import com.example.careercraft.entity.User;
import com.example.careercraft.exception.AppException;
import com.example.careercraft.exception.NotFoundException;
import com.example.careercraft.repository.UserRepository;
import com.example.careercraft.security.JwtTokenProvider;
import com.example.careercraft.security.UserPrincipal;
import com.example.careercraft.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;




    @Override
    public JwtResponse login(LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        JwtResponse jwtResponse = jwtTokenProvider.generateTokens(userPrincipal);
        return new JwtResponse(jwtResponse.getAccessToken(),jwtResponse.getRefreshToken());
    }


    @Override
    public JwtResponse refreshAccessTokenAndGenerateNewToken(String oldToken) {
        if(!jwtTokenProvider.validateToken(oldToken)){
            throw new RuntimeException("none");}
        String email= jwtTokenProvider.getUserEmailFromJwtToken(oldToken);
        return new JwtResponse(jwtTokenProvider.generateTokenFromEmail(email), jwtTokenProvider.generateRefreshTokenFromUsername(email) );
    }


    @Override
    public CustomerInfo getCustomerDetailsFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new AppException(HttpStatus.UNAUTHORIZED, "Authorization header is missing or incorrect.");
        }

        String token = authHeader.substring("Bearer ".length());

        if (!jwtTokenProvider.validateToken(token)) {
            throw new AppException(HttpStatus.UNAUTHORIZED, "Invalid or expired token.");
        }

        Long userIdFromJWT = jwtTokenProvider.getUserIdFromJWT(token);

        User user = userRepository.findById(userIdFromJWT)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "User with ID " + userIdFromJWT + " not found."));

        if (user.getCustomer() == null) {
            throw new AppException(HttpStatus.NOT_FOUND, "Customer not associated with user ID " + userIdFromJWT + ".");
        }

        Customer customer = user.getCustomer();
        return new CustomerInfo(customer.getId(), customer.getName(), customer.getSurname());
    }

}
