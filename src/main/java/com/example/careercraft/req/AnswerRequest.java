package com.example.careercraft.req;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class AnswerRequest {

    @NotBlank(message = "Answer text is required")
    private String text;

    @NotNull(message = "Answer score is required")
    @Positive
    private double score;

    @NotBlank(message = "Priority level is required")
    private String priority; // Уровень приоритета

    private Integer orderValue;

}