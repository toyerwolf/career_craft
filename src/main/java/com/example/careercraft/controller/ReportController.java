package com.example.careercraft.controller;

import com.example.careercraft.dto.CustomerInfo;
import com.example.careercraft.dto.ReportDto;
import com.example.careercraft.service.AuthService;
import com.example.careercraft.service.ReportService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("reports")
@AllArgsConstructor
public class ReportController {
    private final ReportService reportService;
    private final AuthService authService;



    @GetMapping("")
    @Secured("USER")
    public ResponseEntity<List<ReportDto>> generateReports(
            @RequestHeader(value = "Authorization") String authHeader,
            @RequestParam List<Long> skillIds) {
        CustomerInfo customerInfo = authService.getCustomerDetailsFromToken(authHeader);
        List<ReportDto> reports = reportService.generateReportForSkills(customerInfo.getId(), skillIds);
        return ResponseEntity.ok(reports);
    }

}



