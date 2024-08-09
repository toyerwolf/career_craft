package com.example.careercraft.service;

import com.example.careercraft.dto.AnswerDto;
import com.example.careercraft.dto.AnswerIdsDTO;
import com.example.careercraft.entity.Answer;
import com.example.careercraft.entity.Question;
import com.example.careercraft.req.AnswerRequest;
import com.example.careercraft.response.AnswerResponse;

import java.util.List;

public interface AnswerService {

    List<Answer> createAnswersForQuestion(Question question, List<AnswerRequest> answerRequests);

    public List<Long> getAnswerIdsForQuestion(Long questionId);

    List<AnswerResponse> getAnswersByQuestionId(Long questionId);

}
