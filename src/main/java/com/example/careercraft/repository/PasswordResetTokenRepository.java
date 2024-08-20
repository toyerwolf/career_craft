package com.example.careercraft.repository;

import com.example.careercraft.entity.PasswordResetToken;
import com.example.careercraft.entity.User;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    @Modifying
    @Query("UPDATE PasswordResetToken t SET t.invalidated = true WHERE t.user = :user AND t.expiryDate < :now")
    void invalidateExpiredTokensByUser(@Param("user") User user, @Param("now") LocalDateTime now);

    @Query("SELECT t FROM PasswordResetToken t WHERE t.token = :token")
    Optional<PasswordResetToken> findByToken(@Param("token") String token);

    // Метод для поиска по пользователю и проверке, что токен не недействителен
    @Query("SELECT t FROM PasswordResetToken t WHERE t.user = :user AND t.invalidated = false")
    List<PasswordResetToken> findValidTokensByUser(@Param("user") User user);
}