package com.example.careercraft.dto;


import lombok.Data;

@Data
public class AggregatedReportDto {

    private Long customerId;
    private Long categoryId;
//    private String categoryName;
    private double totalScore;
    private double averagePercentageCorrect;
    private String skillLevel;
}
