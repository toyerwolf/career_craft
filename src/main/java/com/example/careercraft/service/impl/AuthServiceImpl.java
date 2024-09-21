package com.example.careercraft.service.impl;

import com.example.careercraft.dto.CustomerInfo;
import com.example.careercraft.dto.JwtResponse;
import com.example.careercraft.dto.LoginDto;
import com.example.careercraft.entity.Customer;
import com.example.careercraft.entity.User;
import com.example.careercraft.exception.AppException;
import com.example.careercraft.exception.CustomAuthenticationException;
import com.example.careercraft.exception.NotFoundException;
import com.example.careercraft.repository.CustomerRepository;
import com.example.careercraft.repository.UserRepository;
import com.example.careercraft.req.CustomerUpdateRequest;
import com.example.careercraft.security.JwtTokenProvider;
import com.example.careercraft.security.UserPrincipal;
import com.example.careercraft.service.AuthService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;




    @Override
    public JwtResponse login(LoginDto loginDto) {
        try {
            // Аутентификация пользователя с использованием предоставленных имени пользователя и пароля
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));

            // Установка объекта аутентификации в контексте безопасности
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Получение данных аутентифицированного пользователя
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

            // Генерация JWT токенов для аутентифицированного пользователя
            JwtResponse jwtResponse = jwtTokenProvider.generateTokens(userPrincipal);

            // Возврат JWT токенов, упакованных в объект JwtResponse
            return new JwtResponse(jwtResponse.getAccessToken(), jwtResponse.getRefreshToken());
        } catch (AuthenticationException ex) {
            // Обработка исключения аутентификации
            throw new CustomAuthenticationException("Invalid username or password");
        }
    }


    @Override
    public JwtResponse refreshAccessTokenAndGenerateNewToken(String oldToken, String refreshToken) {
        // Проверка действительности старого токена
        if (!isValidToken(oldToken)) {
            // Если access token недействителен, проверяем refresh token
            if (isValidToken(refreshToken)) {
                // Генерация новых токенов на основе refresh token
                return generateNewTokensUsingRefreshToken(refreshToken);
            } else {
                // Если оба токена недействительны, выбрасываем исключение
                throw new AppException(HttpStatus.UNAUTHORIZED, "Invalid tokens");
            }
        }

        // Если access token действителен, просто возвращаем новый access token и refresh token
        return generateNewTokensUsingOldToken(oldToken);
    }

    // Вспомогательный метод для проверки действительности токена
    private boolean isValidToken(String token) {
        return jwtTokenProvider.validateToken(token);
    }

    // Вспомогательный метод для генерации новых токенов на основе действительного старого токена
    private JwtResponse generateNewTokensUsingOldToken(String oldToken) {
        String username = jwtTokenProvider.getUserEmailFromJwtToken(oldToken);
        return new JwtResponse(
                jwtTokenProvider.generateTokenFromEmail(username),
                jwtTokenProvider.generateRefreshTokenFromUsername(username)
        );
    }

    // Вспомогательный метод для генерации новых токенов на основе refresh токена
    private JwtResponse generateNewTokensUsingRefreshToken(String refreshToken) {
        String username = jwtTokenProvider.getUserEmailFromJwtToken(refreshToken);
        return new JwtResponse(
                jwtTokenProvider.generateTokenFromEmail(username),
                jwtTokenProvider.generateRefreshTokenFromUsername(username)
        );
    }


    public CustomerInfo getCustomerDetailsFromToken(String authHeader) {
        validateAuthHeader(authHeader);
        Long userIdFromJWT = getUserIdFromToken(authHeader);
        User user = getUserById(userIdFromJWT);
        Customer customer = getCustomerFromUser(user);
        return buildCustomerInfo(customer, user.getEmail());
    }

    @Transactional
    public CustomerInfo updateCustomerDetails(String authHeader, CustomerUpdateRequest updateRequest) {
        validateAuthHeader(authHeader);
        Long userIdFromJWT = getUserIdFromToken(authHeader);
        User user = getUserById(userIdFromJWT);
        Customer customer = getCustomerFromUser(user);
        updateCustomerInfo(customer, updateRequest);
        updateUserEmail(user, updateRequest);
        customerRepository.save(customer);
        userRepository.save(user);
        return buildCustomerInfo(customer, user.getEmail());
    }

    private void validateAuthHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new AppException(HttpStatus.UNAUTHORIZED, "Authorization header is missing or incorrect.");
        }
    }

    private Long getUserIdFromToken(String authHeader) {
        String token = authHeader.substring("Bearer ".length());

        if (!jwtTokenProvider.validateToken(token)) {
            throw new AppException(HttpStatus.UNAUTHORIZED, "Invalid or expired token.");
        }

        return jwtTokenProvider.getUserIdFromJWT(token);
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "User with ID " + userId + " not found."));
    }

    private Customer getCustomerFromUser(User user) {
        if (user.getCustomer() == null) {
            throw new AppException(HttpStatus.NOT_FOUND, "Customer not associated with user ID " + user.getId() + ".");
        }
        return user.getCustomer();
    }

    private void updateCustomerInfo(Customer customer, CustomerUpdateRequest updateRequest) {
        customer.setName(updateRequest.getName());
        customer.setSurname(updateRequest.getSurname());
        customer.setAddress(updateRequest.getAddress());
//        customer.setRegisteredAt(updateRequest.getRegisteredAt());
    }

    private void updateUserEmail(User user, CustomerUpdateRequest updateRequest) {
        if (updateRequest.getEmail() != null && !updateRequest.getEmail().equals(user.getEmail())) {
            user.setEmail(updateRequest.getEmail());
        }
    }

    private CustomerInfo buildCustomerInfo(Customer customer, String email) {
        return new CustomerInfo(
                customer.getId(),
                customer.getName(),
                customer.getSurname(),
                customer.getAddress(),
                email
        );
    }
}


