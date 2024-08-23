package com.example.careercraft.service;

import com.example.careercraft.dto.SkillAssessmentDto;
import com.example.careercraft.entity.SkillAssessment;
import com.example.careercraft.req.SkillAssessmentRequest;

public interface SkillAssessmentService {

    public SkillAssessmentDto saveAssessment(String authHeader, SkillAssessmentRequest skillAssessmentRequest);

    public SkillAssessmentDto getAssessment(String authHeader, Long skillId);
}
