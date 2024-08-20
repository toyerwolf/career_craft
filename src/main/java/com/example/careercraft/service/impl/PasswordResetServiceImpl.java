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
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private UserRepository userRepository;
    private PasswordResetTokenRepository passwordResetTokenRepository;
    private PasswordEncoder passwordEncoder;

    private JavaMailSender mailSender;

    @Transactional
    public void validateResetToken(String token) {
        Optional<PasswordResetToken> optionalToken = passwordResetTokenRepository.findByToken(token);
        if (optionalToken.isEmpty() || optionalToken.get().isExpired() || optionalToken.get().isInvalidated()) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Invalid or expired token");
        }
    }

    @Transactional
    @Override
    public void resetPassword(String token, String newPassword, String confirmPassword) {
        // Найдите токен в базе данных
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new NotFoundException("Invalid or expired token"));
        validateResetToken(token);
        // Найдите пользователя, связанного с токеном
        User user = passwordResetToken.getUser();

        // Проверка совпадения нового пароля и подтверждения пароля
        if (!newPassword.equals(confirmPassword)) {
            throw new AppException(HttpStatus.CONFLICT,"Passwords do not match");
        }

        // Проверка, совпадает ли новый пароль со старым
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new AppException(HttpStatus.BAD_REQUEST,"New password cannot be the same as the old password");
        }

        // Обновите пароль пользователя
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Удалите или деактивируйте токен после использования
        passwordResetTokenRepository.delete(passwordResetToken);
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

        // Установите срок действия токена (например, 10 минут)
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(1);

        // Сохраните токен в базе данных
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(expiryDate);
        resetToken.setInvalidated(false); // Токен еще не использован
        passwordResetTokenRepository.save(resetToken);

        // Отправьте email с инструкциями по сбросу пароля
        sendPasswordResetEmail(user.getEmail(), token);
    }

    private String generateFourDigitToken() {
        Random random = new Random();
        int token = 1000 + random.nextInt(9000); // Генерация случайного 4-значного числа
        return String.valueOf(token);
    }

    private void sendPasswordResetEmail(String email, String token) {
        String resetUrl =  "http://44.203.152.52:8070/api/password/reset"; // URL без параметров
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