package com.example.careercraft.req;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserRegistrationReq {

    @NotEmpty(message = "Username cannot be empty")
    @Email
    private String email;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9!@#$%^&*()-_=+]{8,12}$",message = "password is not required")
    private String password;

    @NotBlank(message = "Surname is required")
    private String name;

    @NotBlank(message = "Surname is required")
    private String surname;

    private String address;


}