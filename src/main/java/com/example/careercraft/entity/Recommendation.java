package com.example.careercraft.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recommendation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;


    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Enumerated(EnumType.STRING) // Использовать строки для хранения значений перечисления
    private RecommendationLevel level;


    private String action;

    @Embedded
    private AdditionalResources additionalResources;


    @ManyToOne
    @JoinColumn(name = "skill_id",nullable = false)
    private Skill skill;

    private String imagePath;

    private boolean isDeleted;

}