package com.example.careercraft.service;


public interface PasswordResetService {

//    void validateResetToken(String token);

    void resetPassword(String token, String newPassword, String confirmPassword);

    public void requestPasswordReset(String email);
}