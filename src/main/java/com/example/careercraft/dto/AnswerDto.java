package com.example.careercraft.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class AnswerDto {

    @NotBlank(message = "Answer text is required")
    private String text;

    @NotNull(message = "Answer score is required")
    @Positive
    private double score;

}
