package com.example.careercraft.service.impl;

import com.example.careercraft.entity.PasswordResetToken;
import com.example.careercraft.entity.User;
import com.example.careercraft.exception.AppException;
import com.example.careercraft.exception.NotFoundException;
import com.example.careercraft.repository.PasswordResetTokenRepository;
import com.example.careercraft.repository.UserRepository;
import com.example.careercraft.service.PasswordResetService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class PasswordResetServiceImpl implements PasswordResetService {

    private UserRepository userRepository;
    private PasswordResetTokenRepository passwordResetTokenRepository;
    private PasswordEncoder passwordEncoder;

    private JavaMailSender mailSender;

    private final Random random = new Random();

    private void validateResetToken(String token) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new NotFoundException("Invalid or expired token"));

        // Если токен истёк, инвалидируйте его
        if (passwordResetToken.isExpired()) {
            passwordResetToken.setInvalidated(true);
            log.info("Saving token: {}", passwordResetToken);
            passwordResetTokenRepository.save(passwordResetToken);
            log.info("Token saved successfully: id={}, token={}, expired={}",
                    passwordResetToken.getId(),
                    passwordResetToken.getToken(),
                    passwordResetToken.isExpired());
            throw new AppException(HttpStatus.BAD_REQUEST, "Token has expired");
        }

        if (passwordResetToken.isInvalidated()) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Token has already been used");
        }
    }
    @Transactional
    @Override
    public void resetPassword(String token, String newPassword, String confirmPassword) {
        validateResetToken(token);
        // Проверяем токен и его состояние
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new NotFoundException("Invalid or expired token"));



        User user = passwordResetToken.getUser();

        if (!newPassword.equals(confirmPassword)) {
            throw new AppException(HttpStatus.CONFLICT, "Passwords do not match");
        }

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "New password cannot be the same as the old password");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Инвалидируем токен
        passwordResetToken.setInvalidated(true);
        passwordResetTokenRepository.save(passwordResetToken);

        log.info("Password reset successfully for user: {}", user.getEmail());
    }

    @Transactional
    @Override
    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User with email " + email + " not found"));

        // Инвалидировать все просроченные токены для пользователя
        passwordResetTokenRepository.invalidateExpiredTokensByUser(user, LocalDateTime.now());

        // Генерация 4-значного числового токена
        String token = generateFourDigitToken();

        // Установите срок действия токена (например, 1 минуту)
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(3);

        // Создайте новый токен и добавьте его к пользователю
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(expiryDate);
        resetToken.setInvalidated(false);
        passwordResetTokenRepository.save(resetToken);

        // Отправьте email с инструкциями по сбросу пароля
        sendPasswordResetEmail(user.getEmail(), token);
    }

    private String generateFourDigitToken() {
        int token = 1000 + random.nextInt(9000); // Генерация случайного 4-значного числа
        return String.valueOf(token);
    }


    private void sendPasswordResetEmail(String email, String token) {
        String resetUrl =  "https://localhost:5173/auth/password/reset"; // URL без параметров
        String subject = "Password Reset Request";
        String message = String.format(
                "To reset your password, please use the following link:\n\n%s\n\n" +
                        "Include the following token in your request body:\n%s\n\n" +
                        "If you did not request this, please ignore this email.",
                resetUrl, token
        );

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);
        mailMessage.setFrom("sys-admin@domain.com");
        mailSender.send(mailMessage);
    }


}