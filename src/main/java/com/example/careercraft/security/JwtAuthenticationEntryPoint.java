package com.example.careercraft.security;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        String errorMessage = "Unauthorized: Token is expired or invalid.";
        int status = HttpServletResponse.SC_UNAUTHORIZED;

        if (authException.getCause() instanceof ExpiredJwtException expiredJwtException) {
            Instant expirationTime = expiredJwtException.getClaims().getExpiration().toInstant();

            errorMessage = String.format("Unauthorized: Token expired at %s.", expirationTime);
        }

        // Логирование сообщения об ошибке
        log.error("Unauthorized error: {}", errorMessage);

        // Отправка ответа с кодом 401 и сообщением об ошибке
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write(String.format("{\"message\": \"%s\"}", errorMessage));
    }
}

