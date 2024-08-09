package com.example.careercraft.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestCompletionResponse  {

    private boolean testCompleted;
    private List<ReportDto> reports;
    private String message;



}