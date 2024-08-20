package com.example.careercraft.dto;

import com.example.careercraft.entity.SkillLevel;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SkillReportDto {
    private Long reportId;
    private Long customerId;
    private Long skillId;
    private String skillName; // Новое поле для названия скилла
    private BigDecimal score;
    private double percentageCorrect;
    private SkillLevel skillLevel;
    private boolean valid;

}