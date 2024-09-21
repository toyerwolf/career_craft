package com.example.careercraft.controller;

import com.example.careercraft.dto.AggregatedReportDto;
import com.example.careercraft.dto.CustomerInfo;
import com.example.careercraft.dto.ReportDto;
import com.example.careercraft.exception.PdfGenerationException;
import com.example.careercraft.service.AuthService;
import com.example.careercraft.service.PdfService;
import com.example.careercraft.service.ReportService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("reports")
@AllArgsConstructor
public class ReportController {
    private final ReportService reportService;
    private final AuthService authService;

    private final PdfService pdfService;



    @GetMapping("")
    @Secured("USER")
    @CrossOrigin(origins = "https://career-craft.netlify.app")
    public ResponseEntity<List<AggregatedReportDto>> generateReports(
            @RequestHeader(value = "Authorization") String authHeader,
            @RequestParam Long categoryId) {

        // Получаем информацию о клиенте из токена
        CustomerInfo customerInfo = authService.getCustomerDetailsFromToken(authHeader);

        // Генерируем отчеты для указанных навыков и категории
        List<AggregatedReportDto> reports = reportService.generateReportForSkills(customerInfo.getId(), categoryId);

        // Возвращаем результат
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/aggregated")
    @Secured("USER")
    @CrossOrigin(origins = "https://career-craft.netlify.app")
    public ResponseEntity<AggregatedReportDto> getAggregatedReportForCategory(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam Long categoryId) {
        AggregatedReportDto aggregatedReportDto = reportService.getAggregatedReportForCategory(authHeader, categoryId);
        return ResponseEntity.ok(aggregatedReportDto);
    }

    @GetMapping("/category/{categoryId}")
    @Secured("USER")
    @CrossOrigin(origins = "https://career-craft.netlify.app")
    public ResponseEntity<List<ReportDto>> getAllReportsForCategoryAndCustomer(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long categoryId) {

        // Получение всех отчетов по категории для определенного пользователя
        List<ReportDto> reports = reportService.getAllReportsForCategoryAndCustomer(authHeader, categoryId);

        return ResponseEntity.ok(reports);
    }


    @GetMapping("/download")
    @CrossOrigin(origins = "https://career-craft.netlify.app")
    public void downloadReportsAsPdf(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam Long categoryId,
            HttpServletResponse response) {
        AggregatedReportDto aggregatedReport = reportService.getAggregatedReportForCategory(authHeader, categoryId);
        List<ReportDto> reports = reportService.getAllReportsForCategoryAndCustomer(authHeader, categoryId);
        pdfService.generateAndWritePdf(reports, aggregatedReport, response);
    }

}



