package com.example.careercraft.req;

import com.example.careercraft.entity.SkillLevel;
import lombok.Data;


@Data
public class SkillAssessmentRequest {

    private Long skillId;
    private SkillLevel skillLevel;
}
