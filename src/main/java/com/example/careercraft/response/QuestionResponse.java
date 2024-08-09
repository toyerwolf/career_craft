package com.example.careercraft.response;

import com.example.careercraft.dto.JobDto;
import com.example.careercraft.entity.Answer;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuestionResponse {

    private Long id;
    private String text;
    private Long jobId; // Идентификатор работы, к которой относится вопрос
    private List<AnswerResponse> answers;
    private String message;
//    private JobDto jobDto;


    // Можно добавить другие необходимые поля, если есть

}