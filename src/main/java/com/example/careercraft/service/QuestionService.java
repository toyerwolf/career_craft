package com.example.careercraft.service;



import com.example.careercraft.dto.QuestionIdsDto;
import com.example.careercraft.dto.SkillQuestionResponse;
import com.example.careercraft.req.QuestionRequest;

import com.example.careercraft.response.QuestionResponse;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface QuestionService {

    QuestionResponse createQuestion(Long jobId, QuestionRequest questionRequest);

    QuestionResponse getQuestionById(Long questionId);
    List<QuestionResponse> getQuestionsByJobId(Long jobId);

    QuestionResponse getNextQuestion(Long currentQuestionId);

    SkillQuestionResponse findQuestionsGroupedBySkills(Collection<Long> skillIds,Long jobId, Long id);

    public QuestionResponse findNextQuestion(Long currentQuestionId, Long skillId, Long jobId);

    public QuestionResponse getPreviousQuestion(Long skillId, Long jobId, Long currentQuestionId);

    public List<QuestionIdsDto> getAllQuestions();
}
