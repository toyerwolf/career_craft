package com.example.careercraft.service;

import com.example.careercraft.dto.AggregatedReportDto;
import com.example.careercraft.dto.ReportDto;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public interface PdfService {

//    byte[] generatePdf(List<ReportDto> reports, AggregatedReportDto aggregatedReport);


    public void generateAndWritePdf(List<ReportDto> reports, AggregatedReportDto aggregatedReport, HttpServletResponse response);
}
