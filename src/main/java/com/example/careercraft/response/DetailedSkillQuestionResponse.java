package com.example.careercraft.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DetailedSkillQuestionResponse {

    private SkillCategory category; // Информация о категории
        private Skill skill; // Информация о навыке
        private Job job; // Информация о работе
        private List<QuestionResponse> questions; // Список вопросов

        @Data
        public static class SkillCategory {
            private Long id;
            private String categoryName;
        }

        @Data
        public static class Skill {
            private Long skillId;
            private String skillName;
        }

        @Data
        public static class Job {
            private Long jobId;
            private String jobName;
        }

        @Data
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class QuestionResponse {
            private String header;
            private Long questionId;
            private String text;
            private Long jobId; // Идентификатор работы, к которой относится вопрос
            private List<AnswerResponse> answers;
            private String message;
        }

        @Data
        public static class AnswerResponse {
            private Long answerId;
            private String text;
            private double score;
            private String priority;
            private Integer orderValue;
        }
    }

