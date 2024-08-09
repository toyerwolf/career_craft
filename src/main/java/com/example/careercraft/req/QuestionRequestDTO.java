package com.example.careercraft.req;

import lombok.Data;

import java.util.Collection;

@Data
public class QuestionRequestDTO {
    private Collection<Long> skillIds;
    private Long jobId;
    private Long currentQuestionId;
}
