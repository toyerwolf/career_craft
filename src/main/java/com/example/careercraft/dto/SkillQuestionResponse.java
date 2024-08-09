package com.example.careercraft.dto;


import com.example.careercraft.response.QuestionResponse;
import lombok.Data;

import java.util.List;

@Data
public class SkillQuestionResponse {

    private List<SkillQuestion> skillsQuestions;

    // Getters and Setters

    @Data
    public static class SkillQuestion {
        private String skillName;
        private Job job; // Добавлен Job
        private List<QuestionResponse> questions;

        // Getters and Setters
    }


    @Data
    public static class Job {
        private Long id;
        private String name;

        // Getters and Setters
    }
}



