package com.example.careercraft.service;

import com.example.careercraft.dto.AggregatedReportDto;
import com.example.careercraft.dto.ReportDto;

import java.util.List;

public interface ReportService {
    public List<AggregatedReportDto> generateReportForSkills(Long customerId, Long categoryId);

    public Long getReportId(List<Long> skillIds);

    public AggregatedReportDto getAggregatedReportForCategory(String authHeader, Long categoryId);
}
