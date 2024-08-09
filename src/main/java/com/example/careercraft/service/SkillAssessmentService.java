package com.example.careercraft.service;

import com.example.careercraft.dto.SkillAssessmentDto;
import com.example.careercraft.entity.SkillAssessment;

public interface SkillAssessmentService {

   void saveAssessment(SkillAssessmentDto assessmentDto);

    SkillAssessmentDto getAssessment(Long userId, Long skillId);
}
