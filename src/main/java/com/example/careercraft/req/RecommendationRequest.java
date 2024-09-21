package com.example.careercraft.req;

import com.example.careercraft.entity.AdditionalResources;
import com.example.careercraft.entity.RecommendationLevel;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationRequest {
    @Enumerated(EnumType.STRING) // Перечисление для уровня рекомендации
    private RecommendationLevel level;
    private Long skillId;
    private Long categoryId; // ID категории
    private String description; // Имя категории
    private String action;
    private AdditionalResources additionalResources;

}
