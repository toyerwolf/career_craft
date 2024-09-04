package com.example.careercraft.controller;

import com.example.careercraft.req.ResetPasswordRequest;
import com.example.careercraft.service.PasswordResetService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/password")
@AllArgsConstructor
public class PasswordResetController {

    private final PasswordResetService passwordResetService;


    @PostMapping("/reset-request")
    public ResponseEntity<String> requestPasswordReset(
            @RequestParam("email") @NotBlank @Email String email) {
        passwordResetService.requestPasswordReset(email);
        return ResponseEntity.ok("Password reset email sent");
    }



    @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(
            @Valid @RequestBody ResetPasswordRequest resetDto) {
        passwordResetService.resetPassword(
                resetDto.getToken(),
                resetDto.getNewPassword(),
                resetDto.getConfirmPassword()
        );
        return ResponseEntity.ok("Password reset successful");
    }
}