package com.example.careercraft.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CustomerUpdateRequest {

    @NotBlank(message = "Name must not be blank")
    @Size(min = 1, max = 50, message = "Name must be between 1 and 50 characters")
    private String name;

    @NotBlank(message = "Surname must not be blank")
    @Size(min = 1, max = 50, message = "Surname must be between 1 and 50 characters")
    private String surname;

    @Size(max = 255, message = "Address must be less than 255 characters")
    private String address;

    @Email(message = "Email should be valid")
    private String email;
}
