package com.example.careercraft.controller;

import com.example.careercraft.dto.SkillAssessmentDto;
import com.example.careercraft.req.SkillAssessmentRequest;
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





    @Secured("USER")
    @PostMapping
    public ResponseEntity<SkillAssessmentDto> saveAssessment(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody SkillAssessmentRequest skillAssessmentRequest) {

        SkillAssessmentDto savedAssessmentDto = skillAssessmentService.saveAssessment(authHeader, skillAssessmentRequest);

        return ResponseEntity.ok(savedAssessmentDto);
    }


    @Secured("USER")
    @GetMapping("/{skillId}")
    public ResponseEntity<SkillAssessmentDto> getAssessment(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long skillId) {

        SkillAssessmentDto assessmentDto = skillAssessmentService.getAssessment(authHeader, skillId);
        return ResponseEntity.ok(assessmentDto);
    }
}