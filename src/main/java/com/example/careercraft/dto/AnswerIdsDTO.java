package com.example.careercraft.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnswerIdsDTO {
    private Long questionId; // Идентификатор вопроса
    private List<Long> answerIds; // Список идентификаторов ответов


}