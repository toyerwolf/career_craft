package com.example.careercraft.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Objects;

@Entity
@Data


public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true) // уникальный текст ответа для каждого вопроса
    private String text;


    @Column(nullable = false)
    private double score;

    @Column(nullable = false)
    private Integer orderValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(nullable = false)
    private String priority;


    @Override
    public String toString() {
        return "Answer{id=" + id + ", text='" + text + "', score=" + score + ", orderValue=" + orderValue + ", priority='" + priority + "'}";
    }

}