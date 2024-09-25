package com.example.careercraft.dto;

import lombok.Data;

@Data
public class ApiResponse<T> {
    private String message;
    private T data;

    // Конструкторы, геттеры и сеттеры
}