package com.example.careercraft.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnswerResponse {

    private Long id;
    private String text;
    private double score;
    private String priority;
    private Integer orderValue;

}