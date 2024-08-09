package com.example.careercraft.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionDto {

    private Long id;

    @NotBlank(message = "Question text is required")
    private String text;

    @Valid
    @NotEmpty(message = "Answers list must not be empty")
    @Size(min = 4, max = 4, message = "There should be exactly 4 answers per question")
    private List<AnswerDto> answers;

}