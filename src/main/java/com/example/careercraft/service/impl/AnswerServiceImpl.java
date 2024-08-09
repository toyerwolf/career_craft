package com.example.careercraft.service.impl;

import com.example.careercraft.dto.AnswerIdsDTO;
import com.example.careercraft.entity.Answer;
import com.example.careercraft.entity.Question;
import com.example.careercraft.exception.AlreadyExistException;
import com.example.careercraft.repository.AnswerRepository;
import com.example.careercraft.req.AnswerRequest;
import com.example.careercraft.response.AnswerResponse;
import com.example.careercraft.service.AnswerService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AnswerServiceImpl implements AnswerService {
    private final AnswerRepository answerRepository;

    @Transactional
    public List<Answer> createAnswersForQuestion(Question question, List<AnswerRequest> answerRequests) {
        List<Answer> answers = new ArrayList<>();
        for (AnswerRequest answerRequest : answerRequests) {
            // Проверяем, существует ли уже ответ с таким текстом для данного вопроса
            boolean exists = answerRepository.existsByTextAndQuestion(answerRequest.getText(), question);
            if (exists) {
                throw new AlreadyExistException("Answer with this text already exists for the question");
            }

            Answer answer = new Answer();
            answer.setText(answerRequest.getText());
            answer.setScore(answerRequest.getScore());
            answer.setOrderValue(answerRequest.getOrderValue());
            answer.setQuestion(question);
            answer.setPriority(answerRequest.getPriority());
            answers.add(answerRepository.save(answer));
        }
        return answers;
    }




    public List<Long> getAnswerIdsForQuestion(Long questionId) {
        return answerRepository.findByQuestionId(questionId).stream()
                .map(Answer::getId)
                .collect(Collectors.toList());
    }

    @Override
    public List<AnswerResponse> getAnswersByQuestionId(Long questionId) {
        // Получаем ответы по questionId и преобразуем в DTO
        List<Answer> answers = answerRepository.findByQuestionId(questionId);
        return answers.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private AnswerResponse convertToDTO(Answer answer) {
        return getAnswerResponse(answer);
    }

    @NotNull
    public static AnswerResponse getAnswerResponse(Answer answer) {
        AnswerResponse dto = new AnswerResponse();
        dto.setId(answer.getId());
        dto.setText(answer.getText());
        dto.setScore(answer.getScore()); // Предположим, что у Answer есть поле score
        dto.setPriority(answer.getPriority()); // Предположим, что у Answer есть поле priority
        dto.setOrderValue(answer.getOrderValue()); // Предположим, что у Answer есть поле orderValue
        return dto;
    }
}
