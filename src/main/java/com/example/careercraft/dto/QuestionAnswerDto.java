package com.example.careercraft.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data

public class QuestionAnswerDto {
    private Long questionId;
    private String questionText;
    private Long answerId;
    private String answerText;
    private double score;
}
