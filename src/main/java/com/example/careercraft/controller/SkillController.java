package com.example.careercraft.controller;

import com.example.careercraft.dto.SkillDTO;
import com.example.careercraft.response.QuestionResponse;
import com.example.careercraft.service.SkillService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
@AllArgsConstructor
public class SkillController {


    private  final SkillService skillService;

    @Secured("USER")
    @GetMapping("/{skillId}/questions")
    public ResponseEntity<List<QuestionResponse>> getAllQuestionsForSkill(
            @PathVariable("skillId") Long skillId) {
        List<QuestionResponse> questions = skillService.getAllQuestionsForSkill(skillId);
        return ResponseEntity.ok(questions);
    }

    @GetMapping
    public List<SkillDTO> getAllSkills() {
        return skillService.getAllSkills();
    }
}
