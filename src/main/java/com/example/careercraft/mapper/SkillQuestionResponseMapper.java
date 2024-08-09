package com.example.careercraft.mapper;

import com.example.careercraft.dto.SkillQuestionResponse;
import com.example.careercraft.entity.Question;
import com.example.careercraft.entity.Skill;
import com.example.careercraft.response.AnswerResponse;
import com.example.careercraft.response.QuestionResponse;
import com.example.careercraft.service.impl.AnswerServiceImpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.careercraft.service.impl.AnswerServiceImpl.getAnswerResponse;

public class SkillQuestionResponseMapper {


    public static SkillQuestionResponse toSkillQuestionResponse(Optional<Question> optionalQuestion) {
        SkillQuestionResponse response = new SkillQuestionResponse();

        if (optionalQuestion.isEmpty()) {
            return response;
        }

        Question question = optionalQuestion.get();
        SkillQuestionResponse.SkillQuestion skillQuestion = new SkillQuestionResponse.SkillQuestion();

        // Устанавливаем название навыка
        skillQuestion.setSkillName(question.getSkills().stream()
                .findFirst()
                .map(Skill::getName)
                .orElse("Unknown"));

        // Устанавливаем информацию о Job
        SkillQuestionResponse.Job job = new SkillQuestionResponse.Job();
        job.setId(question.getJob().getId());
        job.setName(question.getJob().getName());
        skillQuestion.setJob(job);

        // Преобразуем вопрос в QuestionResponse
        QuestionResponse questionResponse = new QuestionResponse();
        questionResponse.setId(question.getId());
        questionResponse.setText(question.getText());
        questionResponse.setJobId(question.getJob().getId());
        questionResponse.setAnswers(question.getAnswers().stream()
                .map(AnswerServiceImpl::getAnswerResponse)
                .collect(Collectors.toList()));

        // Устанавливаем список вопросов в SkillQuestion
        skillQuestion.setQuestions(Collections.singletonList(questionResponse));
        response.setSkillsQuestions(Collections.singletonList(skillQuestion));

        return response;
    }
}

