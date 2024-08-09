package com.example.careercraft.dto;

import com.example.careercraft.response.AnswerResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionIdsDto {

    private Long id;
    private String text;
    private List<AnswerResponse> answers;
}

