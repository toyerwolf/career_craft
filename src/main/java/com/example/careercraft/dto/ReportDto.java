package com.example.careercraft.dto;

import com.example.careercraft.entity.SkillLevel;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ReportDto {
    private Long reportId;
    private Long customerId;
    private BigDecimal score;
    private double percentageCorrect;
//    private List<QuestionAnswerDto> questionAnswers;
    private Long skillId;
    private String skillName;

    private SkillLevel skillLevel;
    private Long categoryId;
    private String categoryName;
    private boolean valid;

}
