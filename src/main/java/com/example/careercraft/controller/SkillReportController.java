package com.example.careercraft.controller;

import com.example.careercraft.dto.SkillReportDto;
import com.example.careercraft.service.ReportService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@AllArgsConstructor
public class SkillReportController {

    private final ReportService skillReportService;

    // Контроллер для получения детализированного отчета по скиллу
    @Secured("USER")
    @GetMapping("/skill/{skillId}")
    public ResponseEntity<SkillReportDto> getSkillReport(@RequestHeader("Authorization") String authHeader,
                                                         @PathVariable Long skillId) {
        // Вызов сервиса для получения отчета по скиллу
        SkillReportDto skillReportDto = skillReportService.getDetailedReportForSkill(authHeader, skillId);

        // Возврат ответа с отчетом
        return ResponseEntity.ok(skillReportDto);
    }
}