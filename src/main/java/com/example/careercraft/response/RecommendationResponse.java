package com.example.careercraft.response;

import com.example.careercraft.entity.RecommendationLevel;
import com.example.careercraft.response.AdditionalResourceResponse;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationResponse {

    @Enumerated(EnumType.STRING) // Перечисление для уровня рекомендации
    private RecommendationLevel level;
    private Long skillId;
    private Long categoryId; // ID категории
    private String description; // Имя категории
    private String action;
    private AdditionalResourceResponse additionalResources;
    private String imageUrl;
    private String skillName;
    private String categoryName;
}