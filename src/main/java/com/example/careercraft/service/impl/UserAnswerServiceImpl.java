package com.example.careercraft.service.impl;

import com.example.careercraft.dto.CustomerInfo;
import com.example.careercraft.dto.ReportDto;

import com.example.careercraft.dto.TestCompletionResponse;
import com.example.careercraft.entity.*;
import com.example.careercraft.exception.AlreadyExistException;
import com.example.careercraft.exception.AppException;
import com.example.careercraft.exception.NotFoundException;

import com.example.careercraft.mapper.QuestionResponseMapper;
import com.example.careercraft.repository.*;
import com.example.careercraft.req.UserAnswerRequest;


import com.example.careercraft.response.AnswerResponse;
import com.example.careercraft.response.QuestionResponse;
import com.example.careercraft.response.QuestionWithReportsResponse;
import com.example.careercraft.service.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class UserAnswerServiceImpl implements UserAnswerService {

    private final UserAnswerRepository userAnswerRepository;


    private final CustomerFinderService customerFinderService;


    private final QuestionRepository questionRepository;


    private final QuestionService questionService;
    private final ReportService reportService;
    private final AnswerService answerService;
    private final AuthService authService;


    @Transactional
    @Override
    public QuestionResponse saveUserAnswerAndGetNextQuestion(String authHeader, UserAnswerRequest userAnswerRequest) {
        // Извлечение информации о клиенте из токена
        CustomerInfo customerInfo = extractCustomerInfo(authHeader);

        // Получение клиента и вопроса
        Customer customer = getCustomer(customerInfo.getId());
        Question question = getQuestion(userAnswerRequest.getQuestionId());

        // Обработка ответа пользователя
        Answer answer = getAnswerByOrderValue(question, userAnswerRequest.getOrderValue());
        Skill skill = getSkill(question);
        handleUserAnswer(customer, question, answer, skill);

        // Поиск следующего вопроса
        QuestionResponse nextQuestion = findNextQuestion(question, skill);

        // Формирование ответа
        return buildQuestionResponse(nextQuestion);
    }



    @Override
    public QuestionWithReportsResponse saveUserAnswerAndGetNextQuestionWithReports(String authHeader, UserAnswerRequest userAnswerRequest) {
        // Извлечение информации о клиенте из токена
        CustomerInfo customerInfo = extractCustomerInfo(authHeader);

        // Получение клиента и вопроса
        Customer customer = getCustomer(customerInfo.getId());
        Question question = getQuestion(userAnswerRequest.getQuestionId());

        // Получение ответа по orderValue
        Answer answer = getAnswerByOrderValue(question, userAnswerRequest.getOrderValue());

        // Получение навыка
        Skill skill = getSkill(question);

        // Проверка существования ответа и сохранение ответа клиента
        validateAndSaveUserAnswer(customer, question, answer, skill);

        // Обработка следующего вопроса или генерация отчета
        return processNextQuestionOrGenerateReport(question, skill, customer);
    }

    private CustomerInfo extractCustomerInfo(String authHeader) {
        return authService.getCustomerDetailsFromToken(authHeader);
    }

    private Customer getCustomer(Long customerId) {
        return customerFinderService.getCustomer(customerId);
    }
    private Question getQuestion(Long questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new NotFoundException("Question not found with id: " + questionId));
    }

    private Answer getAnswerByOrderValue(Question question, Integer orderValue) {
        return question.getAnswers().stream()
                .filter(ans -> ans.getOrderValue().equals(orderValue))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Answer not found with orderValue: " + orderValue + " for question id: " + question.getId()));
    }

    private Skill getSkill(Question question) {
        return question.getSkills().stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundException("No skill found for the question"));
    }

    private void handleUserAnswer(Customer customer, Question question, Answer answer, Skill skill) {
        checkIfAnswerExists(customer, question);
        saveUserAnswer(customer, question, answer, skill);
    }

    private void saveUserAnswer(Customer customer, Question question, Answer answer, Skill skill) {
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setCustomer(customer); // Установить клиента
        userAnswer.setQuestion(question); // Установить вопрос
        userAnswer.setAnswer(answer); // Установить ответ
        userAnswer.setSkill(skill); // Установить навык

        userAnswerRepository.save(userAnswer); // Попытка сохранить ответ
    }

    private void checkIfAnswerExists(Customer customer, Question question) {
        boolean exists = userAnswerRepository.existsByCustomerIdAndQuestionId(customer.getId(), question.getId());
        if (exists) {
            throw new AlreadyExistException("Customer has already answered this question");
        }
    }

    private QuestionResponse findNextQuestion(Question currentQuestion, Skill skill) {
        return questionService.findNextQuestion(currentQuestion.getId(), skill.getId(), currentQuestion.getJob().getId());
    }

    private QuestionResponse buildQuestionResponse(QuestionResponse nextQuestion) {
        if (nextQuestion == null) {
            return QuestionResponse.builder()
                    .message("Test Completed!")
                    .build();
        }

        return QuestionResponse.builder()
                .id(nextQuestion.getId())
                .text(nextQuestion.getText())
                .answers(nextQuestion.getAnswers().stream()
                        .map(ans -> AnswerResponse.builder()
                                .id(ans.getId())
                                .text(ans.getText())
                                .score(ans.getScore())
                                .priority(ans.getPriority())
                                .orderValue(ans.getOrderValue())
                                .build())
                        .collect(Collectors.toList()))
                .message(null)
                .build();
    }

    private void validateAndSaveUserAnswer(Customer customer, Question question, Answer answer, Skill skill) {
        checkIfAnswerExists(customer, question);
        saveUserAnswer(customer, question, answer, skill);
    }

    private QuestionWithReportsResponse processNextQuestionOrGenerateReport(Question currentQuestion, Skill skill, Customer customer) {
        // Поиск следующего вопроса
        QuestionResponse nextQuestion = questionService.findNextQuestion(currentQuestion.getId(), skill.getId(), currentQuestion.getJob().getId());

        if (nextQuestion == null) {
            // Генерация отчета только для навыка последнего вопроса
            List<ReportDto> reports = reportService.generateReportForSkills(customer.getId(), List.of(skill.getId()));

            return QuestionWithReportsResponse.builder()
                    .reports(reports)
                    .build();
        }

        // Создание ответа для следующего вопроса
        List<AnswerResponse> answerResponses = answerService.getAnswersByQuestionId(nextQuestion.getId());

        return QuestionWithReportsResponse.builder()
                .id(nextQuestion.getId())
                .text(nextQuestion.getText())
                .answers(answerResponses)
                .build();
    }
}

