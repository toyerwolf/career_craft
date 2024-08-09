package com.example.careercraft.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // Название навыка

    @ManyToMany(mappedBy = "skills")
    private Set<Job> jobs = new HashSet<>(); // Работы, требующие этот навык

    @ManyToMany(mappedBy = "skills")
    private Set<Question> questions=new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Skill skill = (Skill) o;
        return id != null && id.equals(skill.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }// Вопросы, связанные с этим навыком

    @Override
    public String toString() {
        return "Skill{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", numberOfJobs=" + (jobs != null ? jobs.size() : 0) +
                ", numberOfQuestions=" + (questions != null ? questions.size() : 0) +
                '}';
    }
}