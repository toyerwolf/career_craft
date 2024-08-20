package com.example.careercraft.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class AggregatedReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long customerId;
    private Long categoryId;
    private double totalScore;
    private double averagePercentageCorrect;
    private String skillLevel;
    private boolean valid;
    private String categoryName;

    // Геттеры и сеттеры
}