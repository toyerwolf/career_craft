package com.example.careercraft.mapper;

import com.example.careercraft.entity.Question;
import com.example.careercraft.entity.Skill;
import com.example.careercraft.response.AnswerResponse;
import com.example.careercraft.response.QuestionResponse;
import com.example.careercraft.service.impl.AnswerServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.careercraft.service.impl.AnswerServiceImpl.getAnswerResponse;

public class QuestionResponseMapper {

    public static QuestionResponse toQuestionResponse(Question question) {
        QuestionResponse questionResponse = new QuestionResponse();

        // Заполняем данные о вопросе
        questionResponse.setId(question.getId());
        questionResponse.setText(question.getText());
//        questionResponse.setJobId(question.getJob().getId());

        // Заполняем список ответов
        List<AnswerResponse> answerResponses = question.getAnswers().stream()
                .map(AnswerServiceImpl::getAnswerResponse)
                .collect(Collectors.toList());
        questionResponse.setAnswers(answerResponses);

        // Заполняем список названий навыков
        List<String> skillNames = question.getSkills().stream()
                .map(Skill::getName)
                .toList();

        return questionResponse;
    }
}
