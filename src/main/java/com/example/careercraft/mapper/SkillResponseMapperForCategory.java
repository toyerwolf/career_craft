package com.example.careercraft.mapper;


import com.example.careercraft.entity.Question;
import com.example.careercraft.entity.Skill;
import com.example.careercraft.exception.NotFoundException;
import com.example.careercraft.response.DetailedSkillQuestionResponse;

import com.example.careercraft.util.AnswerResponseUtil;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

public class SkillResponseMapperForCategory {



    public static DetailedSkillQuestionResponse toDetailedSkillQuestionResponse(Optional<Question> optionalQuestion) {
        DetailedSkillQuestionResponse response = new DetailedSkillQuestionResponse();

        if (optionalQuestion.isEmpty()) {
            throw new NotFoundException("Question not found");
        }

        Question question = optionalQuestion.get();

        // Создаем объект SkillCategory и устанавливаем его информацию
        DetailedSkillQuestionResponse.SkillCategory category = new DetailedSkillQuestionResponse.SkillCategory();
        category.setId(question.getSkills().stream()
                .findFirst()
                .map(skill -> skill.getCategory().getId())
                .orElse(null));
        category.setCategoryName(question.getSkills().stream()
                .findFirst()
                .map(skill -> skill.getCategory().getName())
                .orElse("Unknown"));

        // Создаем объект Skill и устанавливаем его информацию
        DetailedSkillQuestionResponse.Skill skill = new DetailedSkillQuestionResponse.Skill();
        skill.setSkillId(question.getSkills().stream()
                .findFirst()
                .map(Skill::getId)
                .orElse(null));
        skill.setSkillName(question.getSkills().stream()
                .findFirst()
                .map(Skill::getName)
                .orElse("Unknown"));

        // Проверяем, существует ли объект Job
        if (question.getJob() == null) {
            throw new NotFoundException("Job not found");
        }
        // Создаем объект Job и устанавливаем его информацию
        DetailedSkillQuestionResponse.Job job = new DetailedSkillQuestionResponse.Job();
        job.setJobId(question.getJob().getId());
        job.setJobName(question.getJob().getName());

        // Создаем список QuestionResponse и заполняем его данными
        DetailedSkillQuestionResponse.QuestionResponse questionResponse = new DetailedSkillQuestionResponse.QuestionResponse();
        questionResponse.setQuestionId(question.getId());
        questionResponse.setText(question.getText());
        questionResponse.setJobId(question.getJob().getId());
        questionResponse.setAnswers(question.getAnswers().stream()
                .map(AnswerResponseUtil::mapAnswerToResponse) // Используем утилитный метод
                .collect(Collectors.toList()));

        // Устанавливаем данные в ответ
        response.setCategory(category);
        response.setSkill(skill);
        response.setJob(job);
        response.setQuestions(Collections.singletonList(questionResponse));

        return response;
    }
}

