package com.example.careercraft.response;

import com.example.careercraft.dto.AggregatedReportDto;
import com.example.careercraft.dto.ReportDto;
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
public class QuestionWithReportsResponse {

    private Long id;
    private String text;
    private Long jobId; // Идентификатор работы, к которой относится вопрос
    private List<AnswerResponse> answers;
    private List<AggregatedReportDto> reports;;
}
