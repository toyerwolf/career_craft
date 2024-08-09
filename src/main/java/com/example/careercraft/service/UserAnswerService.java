package com.example.careercraft.service;

import com.example.careercraft.dto.TestCompletionResponse;
import com.example.careercraft.req.UserAnswerRequest;
import com.example.careercraft.response.QuestionResponse;
import com.example.careercraft.response.QuestionWithReportsResponse;


public interface UserAnswerService {

    public QuestionResponse saveUserAnswerAndGetNextQuestion(String authHeader, UserAnswerRequest userAnswerRequest);

    public QuestionWithReportsResponse saveUserAnswerAndGetNextQuestionWithReports(String authHeader, UserAnswerRequest userAnswerRequest);
}
