package com.example.careercraft.service;

import com.example.careercraft.dto.ReportDto;

import java.util.List;

public interface ReportService {
    public List<ReportDto> generateReportForSkills(Long customerId, List<Long> skillIds);

    public Long getReportId(List<Long> skillIds);
}
