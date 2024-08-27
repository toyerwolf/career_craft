package com.example.careercraft.util;

import com.example.careercraft.entity.Answer;
import com.example.careercraft.response.DetailedSkillQuestionResponse;

public class AnswerResponseUtil {

    public static DetailedSkillQuestionResponse.AnswerResponse mapAnswerToResponse(Answer answer) {
        DetailedSkillQuestionResponse.AnswerResponse response = new DetailedSkillQuestionResponse.AnswerResponse();
        response.setAnswerId(answer.getId());
        response.setText(answer.getText());
        response.setPriority(answer.getPriority());
        response.setScore(answer.getScore());
        response.setOrderValue(answer.getOrderValue());
        return response;
    }
}