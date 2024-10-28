package com.example.careercraft.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.*;

@Entity
@Data
@EqualsAndHashCode(of = "id")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String text;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private Set<Answer> answers = new HashSet<>();


    @ManyToOne
    @JoinColumn(name = "job_id") // Предположим, что вопрос принадлежит конкретной работе
    private Job job;


    @ManyToMany
    @JoinTable(
            name = "question_skill",
            joinColumns = @JoinColumn(name = "question_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private Set<Skill> skills = new HashSet<>();

    @Override
    public String toString() {
        return "Question{id=" + id + ", text='" + text + "'}";
    }

//    @ManyToOne
//    @JoinColumn(name = "category_id") // Добавление связи с категорией
//    private Category category;

}
