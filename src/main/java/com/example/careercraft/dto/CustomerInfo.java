package com.example.careercraft.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerInfo {
    private Long id;
    private String name;
    private String surname;
    private String address;
    private LocalDateTime registeredAt;
    private String email;
}
