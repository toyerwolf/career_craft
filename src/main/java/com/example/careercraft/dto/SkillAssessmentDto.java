package com.example.careercraft.dto;

import com.example.careercraft.entity.SkillLevel;
import lombok.Data;

@Data
public class SkillAssessmentDto {

    private Long customerId;
    private Long skillId;
    private SkillLevel skillLevel;
}
