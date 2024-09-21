package com.example.careercraft.req;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserRegistrationReq {

    @NotEmpty(message = "Username cannot be empty")
    @Email
    private String email;

    @NotBlank(message = "New password is required")
    @Size(min = 8, message = "New password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[@#$%^&+=!]).*$",
            message = "New password must contain at least one letter, one number, and one special character")
    private String password;

    @NotBlank(message = "Surname is required")
    private String name;

    @NotBlank(message = "Surname is required")
    private String surname;

    private String address;


}