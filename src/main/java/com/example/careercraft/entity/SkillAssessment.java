package com.example.careercraft.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class SkillAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer; // Связь с сущностью Customer

    @ManyToOne
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill; // Связь с сущностью Skill

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SkillLevel skillLevel; // Уровень навыка

    // Конструкторы, геттеры и сеттеры (если не используется Lombok)
}