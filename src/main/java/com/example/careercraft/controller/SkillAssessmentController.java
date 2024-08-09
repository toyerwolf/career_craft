package com.example.careercraft.controller;

import com.example.careercraft.dto.SkillAssessmentDto;
import com.example.careercraft.service.SkillAssessmentService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assessments")
@AllArgsConstructor
public class SkillAssessmentController {

    private final SkillAssessmentService skillAssessmentService;


    @PostMapping
    @Secured("USER")
    public ResponseEntity<Void> saveAssessment(@RequestBody SkillAssessmentDto assessmentDto) {
        skillAssessmentService.saveAssessment(assessmentDto);
        return ResponseEntity.ok().build();
    }

    // Получение оценки навыка по userId и skillId
    @GetMapping("/{customerID}/{skillId}")
    public ResponseEntity<SkillAssessmentDto> getAssessment(
            @PathVariable Long customerID,
            @PathVariable Long skillId) {
        SkillAssessmentDto assessmentDto = skillAssessmentService.getAssessment(customerID, skillId);
        return ResponseEntity.ok(assessmentDto);
    }
}