package com.example.careercraft.dto;

import com.example.careercraft.response.AnswerResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuestionIdsDto {

    private Long id;
    private String text;
    private List<AnswerResponse> answers;
    private boolean completed;
    private String message;
}

